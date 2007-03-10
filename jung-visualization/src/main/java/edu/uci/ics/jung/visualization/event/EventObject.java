package edu.uci.ics.jung.visualization.event;

/**
 * A Base class for any input events to be handled by the visualization.
 * @author Jason A Wrang
 *
 */
public class EventObject<E> {
	/**
	 * The object on which the event initially occurred.
	 */
	Object source;
	
	/**
	 * The UI Toolkit specific event object
	 */
	E uiEvent;
	
	public EventObject(EventObject<E> event) {
		this.source = event.source;
		this.uiEvent = event.uiEvent;
	}

	public EventObject(E uiEvent, Object source) {
		this.source = source;
		this.uiEvent = uiEvent;
	}
	
	/**
	 * @return the source
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * @return the uiEvent
	 */
	public E getUiEvent() {
		return uiEvent;
	}
}