package com.iotcore.aws.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.model.event.Event;
import com.iotcore.core.model.event.EventBus;

import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.DeleteRuleRequest;
import software.amazon.awssdk.services.eventbridge.model.DeleteRuleResponse;
import software.amazon.awssdk.services.eventbridge.model.ListRuleNamesByTargetRequest;
import software.amazon.awssdk.services.eventbridge.model.ListRuleNamesByTargetResponse;
import software.amazon.awssdk.services.eventbridge.model.ListRulesRequest;
import software.amazon.awssdk.services.eventbridge.model.ListRulesResponse;
import software.amazon.awssdk.services.eventbridge.model.ListTargetsByRuleRequest;
import software.amazon.awssdk.services.eventbridge.model.ListTargetsByRuleResponse;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResultEntry;
import software.amazon.awssdk.services.eventbridge.model.PutRuleRequest;
import software.amazon.awssdk.services.eventbridge.model.PutRuleResponse;
import software.amazon.awssdk.services.eventbridge.model.PutTargetsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutTargetsResponse;
import software.amazon.awssdk.services.eventbridge.model.RemoveTargetsRequest;
import software.amazon.awssdk.services.eventbridge.model.Rule;
import software.amazon.awssdk.services.eventbridge.model.Target;


/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */

@ApplicationScoped
public class AwsEventBridgeService extends EventBus {

	private static final long serialVersionUID = -1874465418999576208L;

	private static final Logger LOG = LoggerFactory.getLogger(AwsEventBridgeService.class);
	private static final String DEFAULT_BUS = "default";
	
	public static AwsEventBridgeService getInstance() {
		if (_instance == null) {
			_instance = new AwsEventBridgeService();
			_instance.init();
		}
		return (AwsEventBridgeService)_instance;
	}

	private EventBridgeClient client = null;
	
	
	
	/**
	 * Constructor
	 */
	public AwsEventBridgeService() {
		super();
	}
	
	@Override
	@PostConstruct
	public void init() {
		if (_instance == null) {
			LOG.debug("Instance initialized");
			_instance = this;
		}
	}
	
	
	/**
	 * Creates an EventBridge rule. If the rule exists, adds the given targets to it.
	 * @param ruleName
	 * @param busName
	 * @param eventPattern
	 * @param targetArn
	 * @return true if the rule was created succesfully. False otherwise.
	 */
	public boolean createEventRule(String ruleName, String busName, String eventPattern, String... targetArn) {

		boolean ruleExists = false;
		Set<Target> targets = new HashSet<Target>();
		Integer cont = 1;
		for (final String arn : targetArn) {
			targets.add(Target.builder().arn(arn).id(cont.toString()).build());
			cont++;
		}
		
		ListRulesRequest listReq = ListRulesRequest.builder()
				.eventBusName(busName)
				.namePrefix(ruleName)
				.build();
		
		ListRulesResponse listRes = client().listRules(listReq);
		
		for (final Rule rule : listRes.rules()) {
			if (rule.name().equals(ruleName)) {
				
				if (LOG.isTraceEnabled()) {
					LOG.trace("Rule \"{}\" already exists.", ruleName);
				}
				
				ruleExists = true;

			}
		}
		
		
		if (!ruleExists) {
			LOG.trace("Creating rule \"{}\" ", ruleName);
			PutRuleResponse putRuleRes = client().putRule(PutRuleRequest.builder()
					.name(ruleName)
					.eventBusName(busName)
					.eventPattern(eventPattern)
					.build());
			if (!AwsBase.responseOk(putRuleRes)) {
				return false;
			}
		}
		else {
			ListTargetsByRuleResponse tgRes = client().listTargetsByRule(ListTargetsByRuleRequest.builder().rule(ruleName).build());
			for (Target tg : tgRes.targets()) {
				targets.add(tg);
			}
		}

		if (LOG.isTraceEnabled()) {
			StringBuffer tgtStr = new StringBuffer();
			targets.forEach(t -> tgtStr.append( t.arn() + "\n"));
			LOG.trace("Adding targets to rule:\n{}", tgtStr.toString());
		}
		
		
		PutTargetsRequest putTargetsReq = PutTargetsRequest.builder()
				.eventBusName(busName)
				.rule(ruleName)
				.targets(targets)
				.build();

		PutTargetsResponse tgtsRes = client().putTargets(putTargetsReq);
	
		return AwsBase.responseOk(tgtsRes);
	}

	/**
	 * @param ruleName
	 * @param schedulePattern
	 * @param targetArn
	 * @return
	 */
	public boolean createScheduledEventRule(String ruleName, String schedulePattern, String... targetArn) {

		final ListRulesRequest listReq = ListRulesRequest.builder()
				.eventBusName(DEFAULT_BUS)
				.namePrefix(ruleName)
				.build();
		
		final ListRulesResponse listRes = client().listRules(listReq);

		boolean ruleExists = false;
		for (final Rule rule : listRes.rules()) {
			if (rule.name().equals(ruleName)) {
				ruleExists = true;
			}
		}

		List<Target> targets = null;
		if (!ruleExists) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("PutRule request [BusName={}, RuleName={}, EventPattern={}]", DEFAULT_BUS, ruleName,
						schedulePattern);
			}
			final PutRuleRequest putRuleReq = PutRuleRequest.builder()
					.name(ruleName)
					.scheduleExpression(schedulePattern)
					.build();
			
			final PutRuleResponse putRuleRes = client().putRule(putRuleReq);
			if (!AwsBase.responseOk(putRuleRes)) {
				return false;
			}
			targets = new ArrayList<Target>();
		} else {
			if (LOG.isTraceEnabled()) {
				LOG.trace("EventRule \"{}\" exists. Update rule targets.", ruleName);
			}
			final ListTargetsByRuleResponse tgRes = client().listTargetsByRule(
																	ListTargetsByRuleRequest.builder()
																		.rule(ruleName)
																		.build());
			targets = tgRes.targets();
			if (!targets.isEmpty()) {
				for (int i = targets.size() - 1; i >= 0; i--) {
					final Target tg = targets.get(i);
					for (final String arn : targetArn) {
						if (tg.arn().startsWith(arn)) {
							LOG.trace("Replacing target {}", arn);
							targets.remove(i);
						}
					}
				}
			}
		}

		Integer cont = 0;
		for (final String arn : targetArn) {
			targets.add(Target.builder().arn(arn).id(cont.toString()).build());
			cont++;
		}

		final PutTargetsRequest putTargetsReq = PutTargetsRequest.builder().rule(ruleName).targets(targets).build();

		final PutTargetsResponse tgtsRes = client().putTargets(putTargetsReq);

		return AwsBase.responseOk(tgtsRes);

	}

	/**
	 * @param name
	 * @param busName
	 * @return
	 */
	public boolean deleteEventRule(String name, String busName) {
		String bus = DEFAULT_BUS;
		if (busName != null) {
			bus = (busName);
		}

		final ListTargetsByRuleRequest lstTgt = ListTargetsByRuleRequest.builder()
				.eventBusName(bus)
				.rule(name)
				.build();
		final ListTargetsByRuleResponse lstRes = client().listTargetsByRule(lstTgt);

		final List<String> tIds = new ArrayList<String>();
		final List<Target> targets = lstRes.targets();
		targets.forEach(tgt -> tIds.add(tgt.id()));

		final RemoveTargetsRequest tgtReq = RemoveTargetsRequest.builder()
				.eventBusName(bus)
				.rule(name)
				.ids(tIds)
				.build();
		
		client().removeTargets(tgtReq);

		final DeleteRuleRequest req = DeleteRuleRequest.builder()
				.name(name)
				.eventBusName(bus)
				.build();

		final DeleteRuleResponse res = client().deleteRule(req);
		return AwsBase.responseOk(res);
	}

	/**
	 * @param targetArn
	 * @param busName
	 * @return
	 */
	public boolean deleteTarget(String targetArn, String busName) {
		String bus = DEFAULT_BUS;
		if (busName != null) {
			bus = (busName);
		}

		final ListRuleNamesByTargetRequest lstRulesReq = ListRuleNamesByTargetRequest.builder()
				.eventBusName(bus)
				.targetArn(targetArn)
				.build();

		final ListRuleNamesByTargetResponse lstRulesRes = client().listRuleNamesByTarget(lstRulesReq);
		if (!AwsBase.responseOk(lstRulesRes)) {
			return false;
		}

		for (final String rule : lstRulesRes.ruleNames()) {
			LOG.trace("Target is associated with rule {}", rule);
			final ListTargetsByRuleRequest lstTgt = ListTargetsByRuleRequest.builder()
					.eventBusName(bus)
					.rule(rule)
					.build();

			final ListTargetsByRuleResponse lstRes = client().listTargetsByRule(lstTgt);

			final List<String> tIds = new ArrayList<String>();
			final List<Target> targets = lstRes.targets();
			for (int i = targets.size() - 1; i >= 0; i--) {
				final Target tgt = targets.get(i);
				if (tgt.arn().equals(targetArn)) {
					LOG.debug("Removing {} target {} ", rule, targetArn);
					tIds.add(tgt.id());
					targets.remove(i);
				}
			}

			final RemoveTargetsRequest tgtReq = RemoveTargetsRequest.builder()
					.eventBusName(bus)
					.rule(rule)
					.ids(tIds)
					.build();

			client().removeTargets(tgtReq);

			if (targets.isEmpty()) {
				LOG.debug("Rule {} is empty. Deleting it", rule);
				final DeleteRuleRequest req = DeleteRuleRequest.builder()
						.name(rule)
						.eventBusName(bus)
						.build();


				client().deleteRule(req);

			}

		}

		return true;
	}

	/**
	 * @param event
	 * @return
	 */
	@Override
	public boolean publish(String busName, Event<?> event) {
		
		
		// Populate a List with the resource ARN values
		final List<PutEventsRequestEntry> list = new ArrayList<PutEventsRequestEntry>();
		final PutEventsRequestEntry reqEntry = PutEventsRequestEntry.builder()
				.eventBusName(busName)
				.source(event.getSource())
				.detailType(event.getEventName())
				.detail(event.toJson())
				.build();

		list.add(reqEntry);

		LOG.trace("Publishing event entries");
		
		if (LOG.isTraceEnabled()) {
			for (PutEventsRequestEntry entry:list) {
				StringBuffer buff = new StringBuffer();
				buff.append("\n\tBus Name:    ");
				buff.append(entry.eventBusName());
				buff.append("\n\tDetail Type:  ");
				buff.append(entry.detailType());
				buff.append("\n\tEvent Detail: ");
				buff.append(entry.detail());
				
				LOG.trace("PutEvents request entry: {}", buff.toString());
			}
		}

		final PutEventsResponse result = client().putEvents(PutEventsRequest.builder().entries(list).build());

		List<PutEventsResultEntry> resultEntries = result.entries();
		if (resultEntries.size() != 1) {
			LOG.error("Wrong number of result entries ({}) ", resultEntries.size());
		}

		final PutEventsResultEntry resultEntry = resultEntries.get(0);
		if (resultEntry.eventId() == null) {
			LOG.error("Injection failed (ErrorCode={}): {}", resultEntry.errorCode(), resultEntry.errorMessage());
			return false;
		}
		
		event.setId(resultEntry.eventId());
		
		return true;
	}
	

	@Override
	public <E extends Event<E>> Collection<E> publish(Collection<E> evts) {
		
		// Populate a List with the resource ARN values
		final List<PutEventsRequestEntry> list = new ArrayList<PutEventsRequestEntry>();
		evts.forEach(event -> list.add(PutEventsRequestEntry.builder()
				.eventBusName(event.getBusName())
				.source(event.getSource())
				.detailType(event.getEventName())
				.detail(event.toJson())
				.build()));
		
		LOG.trace("Publishing event entries");
		
		if (LOG.isTraceEnabled()) {
			for (PutEventsRequestEntry entry:list) {
				StringBuffer buff = new StringBuffer();
				buff.append("\n\tBus Name:    ");
				buff.append(entry.eventBusName());
				buff.append("\n\tDetail Type:  ");
				buff.append(entry.detailType());
				buff.append("\n\tEvent Detail: ");
				buff.append(entry.detail());
				
				LOG.trace("PutEvents request entry: {}", buff.toString());
			}
		}

		final PutEventsResponse result = client().putEvents(PutEventsRequest.builder().entries(list).build());

		List<E> retEvents = new ArrayList<E>(evts);
		Iterator<E> eIt = retEvents.iterator();
		for (PutEventsResultEntry resEntry:result.entries()) {
			Event<?> evt = null;
			if (eIt.hasNext()) {
				evt = eIt.next();
			}
			evt.setId(resEntry.eventId());
		}
		
		
		return retEvents;
	}


	
	public EventBridgeClient create() {
		return EventBridgeClient.builder().build();
	}
	
	private EventBridgeClient client() {
		if (client == null) {
			client = create();
		}
		return client;
	}

}
