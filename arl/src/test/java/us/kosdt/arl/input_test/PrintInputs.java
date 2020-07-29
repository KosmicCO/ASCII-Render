package us.kosdt.arl.input_test;

import us.kosdt.arl.engine.Game;
import us.kosdt.arl.event.Message;
import us.kosdt.arl.graphics.gui.Component;
import us.kosdt.arl.graphics.gui.GuiManager;
import us.kosdt.arl.util.math.Vec2d;

public class PrintInputs {

    public static void main(String[] args) {
        Game.init();
        GuiManager.GUI_MANAGER.setComponent(new PrintInputsComp());
        Game.run();
    }

    private static class PrintInputsComp implements Component {

        @Override
        public boolean contains(Vec2d v) {
            return true;
        }

        @Override
        public boolean handleMessage(Message message, boolean first) {
            System.out.println(message.toString());
            return true;
        }

        @Override
        public void render() {

        }
    }
}
