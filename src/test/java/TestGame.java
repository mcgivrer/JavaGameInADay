import com.snapgames.framework.GameInterface;

public class TestGame implements GameInterface {
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
}
