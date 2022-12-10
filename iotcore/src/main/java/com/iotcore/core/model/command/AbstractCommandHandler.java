package com.iotcore.core.model.command;

import javax.inject.Inject;

import com.iotcore.core.model.event.IEventBus;
import com.iotcore.core.model.exception.ObjectValidationException;

public abstract class AbstractCommandHandler<C extends AbstractCommand<?>> implements CommandHandler<C> {

	private static final long serialVersionUID = 4495733506687120215L;
	
	/**
	 * @param cmd
	 * @throws ObjectValidationException
	 */
	protected abstract void validate(C cmd) throws ObjectValidationException;
	
	@Inject
	private IEventBus eventBus;
	
	@Override
	public IEventBus getEventBus() {
		return eventBus;
	}

}
