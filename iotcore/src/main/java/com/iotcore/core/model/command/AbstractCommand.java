/**
 * 
 */
package com.iotcore.core.model.command;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.UUID;

import com.iotcore.core.model.exception.ObjectValidationException;

/**
 * @author jmgarcia
 *
 */
public class AbstractCommand<C extends AbstractCommand<C>> implements Command {
	
	private static final long serialVersionUID = -425895518871800394L;
	
	private final String id = UUID.randomUUID().toString();
	private final Date createdOn = new Date();
	
	/**
	 * 
	 */
	protected AbstractCommand() {
		
	}

	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @return the createdOn
	 */
	@Override
	public Date getCreatedOn() {
		return createdOn;
	}
	
	
	/**
	 * @author jmgarcia
	 *
	 * @param <C>
	 */
	public abstract static class Builder<C> {
		
		
		protected abstract boolean dataIsValid() throws ObjectValidationException;
		protected abstract C newInstanceWithData() throws ObjectValidationException;
		
		private Class<C> cmdClass;
		
		/**
		 * @param cmdClass
		 */
		protected Builder(Class<C> cmdClass) {
			this.cmdClass = cmdClass;
		}
		
		
		
		public C build() throws ObjectValidationException {
			
			if (!dataIsValid()) {
				return null;
			}
			return newInstanceWithData();
		}
		
		
		protected C newInstance(Object ... args) {
			try {
				return (cmdClass.getConstructor((Class<?>[])null).newInstance(args));	
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			return null;
		}
	}


}
