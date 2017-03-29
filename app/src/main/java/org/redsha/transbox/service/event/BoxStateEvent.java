package org.redsha.transbox.service.event;

public class BoxStateEvent {
    /**
     * 箱子状态，true打开，false 关闭
     */
    private boolean state;

    /**
     * 展示diglog
     */
    private boolean isShowDlg;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean isShowDlg() {
        return isShowDlg;
    }

    public void setShowDlg(boolean showDlg) {
        isShowDlg = showDlg;
    }
}
