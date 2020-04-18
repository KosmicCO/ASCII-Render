package us.kosdt.arl.graphics.gui;

import us.kosdt.arl.engine.Input;
import us.kosdt.arl.event.Message;

import java.util.Set;
import java.util.TreeSet;

import us.kosdt.arl.event.ParentListener;
import us.kosdt.arl.event.messages.gui.*;
import us.kosdt.arl.graphics.Window;

import static us.kosdt.arl.engine.Input.Type.*;

/**
 * The singleton head for passing and managing the gui structure and key inputs.
 *
 * @author TARS
 */
public class GuiManager {

    public static final GuiManager GUI_MANAGER = new GuiManager();

    private Component component;
    private final Set<Class<? extends Message>> typesToPass;

    private GuiManager() {
        typesToPass = new TreeSet();
        component = null;
        Input.addListener((type, mouse, deltaMouse, key, pressed, changed) -> {
            if(component != null){
                switch(type){
                    case MOUSE:
                        component.handleMessage(new MousePosition(Window.window().toWindowScale(mouse),
                                Window.window().toWindowScale(deltaMouse)));
                        break;
                    case KEY:
                        component.handleMessage(new KeyPress(key, pressed, changed));
                        break;
                    case MOUSE_BUTTON:
                        component.handleMessage(new MouseButton(key, pressed, changed,
                                Window.window().toWindowScale(mouse)));
                        break;
                    case MOUSE_WHEEL:
                        component.handleMessage(new MouseWheel(mouse, deltaMouse));
                        break;
                }
            }
        });
    }

    /**
     * Returns the current component of the manager.
     *
     * @return The current component.
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Adds the message type to the list of types to be passed on to components.
     *
     * @param <M> The message type.
     * @param listener The listener to listen for messages of the given type.
     * @param messageType The message type.
     * @return The id of the message type in listener or -1 if the type was already registered.
     */
    public <M extends Message> int passMessageType(ParentListener listener, Class<M> messageType) {
        int id = -1;
        if (!typesToPass.contains(messageType)) {
            id = listener.addListener(messageType, m -> {
                if (component != null) {
                    component.handleMessage(m);
                }
            });
            typesToPass.add(messageType);
        }
        return id;
    }

    /**
     * Renders the manager's component.
     */
    public void render() {
        if(component != null) {
            component.render();
        }
    }

    /**
     * Sets the current component of the manager.
     *
     * @param component the component to set as the current.
     */
    public void setComponent(Component component){
        this.component = component;
    }
}
