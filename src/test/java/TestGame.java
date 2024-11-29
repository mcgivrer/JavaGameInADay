import com.snapgames.framework.GameInterface;

import javax.swing.*;

public class TestGame extends JPanel implements GameInterface {
    private boolean exit = false;
    private boolean pause = false;
    private int debug = 0;

    @Override
    public void requestExit() {
        this.exit = true;
    }

    @Override
    public void setDebug(int i) {
        this.debug = i;
    }

    @Override
    public int getDebug() {
        return debug;
    }

    public boolean isDebugGreaterThan(int debugLevel) {
        return debug > debugLevel;
    }

    @Override
    public boolean isNotPaused() {
        return pause;
    }

    @Override
    public void setPause(boolean p) {
        this.pause = p;
    }

    @Override
    public void setExit(boolean e) {
        this.exit = e;
    }

    @Override
    public boolean isExitRequested() {
        return exit;
    }
}