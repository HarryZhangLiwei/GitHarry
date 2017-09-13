package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.border.EtchedBorder;
 
/*
 * This class used to draw the window showed on the desktop
 * */
public class Desktop {
 
    // Window width
    private int _width = 500;
 
    // Window height
    private int _height = 300;
 
    //step time
    private int _step = 30;
 
    // each step time
    private int _stepTime = 40;
 
    //Display time
    private int _displayTime = 6000;
 
    // the number of tool
    private int _countOfToolTip = 0;
 
    // max number of tim
    private int _maxToolTip = 0;
 
    // max number of mention 
    private int _maxToolTipSceen;
 
    // font 
    private Font _font;
 
    // color of bond
    private Color _bgColor;
 
    // color of bg
    private Color _border;
 
    // message color
    private Color _messageColor;
 
    // gap
    int _gap;
 
    // set top
    boolean _useTop = true;
 
    /**
     * Structure
     * @describe set initial values of the window
     */
    public Desktop() {
        
        _font = new Font("宋体", 0, 12);
        _bgColor = new Color(255, 255, 225);
        _border = Color.BLACK;
        _messageColor = Color.BLACK;
        _useTop = true;
        try {
            JWindow.class.getMethod("setAlwaysOnTop",
                    new Class[] { Boolean.class });
        } catch (Exception e) {
            _useTop = false;
        }
 
    }
 
    /**
     * Reconstruct the window used to show the window
     */
    class ToolTipSingle extends JWindow {
        private static final long serialVersionUID = 1L;
 
        private JLabel _iconLabel = new JLabel();
 
        private JTextArea _message = new JTextArea();
 
        public ToolTipSingle() {
            initComponents();
        }
 
        private void initComponents() {
            setSize(_width, _height);
            _message.setFont(getMessageFont());
            JPanel externalPanel = new JPanel(new BorderLayout(1, 1));
            externalPanel.setBackground(_bgColor);
            JPanel innerPanel = new JPanel(new BorderLayout(getGap(), getGap()));
            innerPanel.setBackground(_bgColor);
            _message.setBackground(_bgColor);
            _message.setMargin(new Insets(4, 4, 4, 4));
            _message.setLineWrap(true);
            _message.setWrapStyleWord(true);
            EtchedBorder etchedBorder = (EtchedBorder) BorderFactory
                    .createEtchedBorder();
            externalPanel.setBorder(etchedBorder);
            externalPanel.add(innerPanel);
            _message.setForeground(getMessageColor());
            innerPanel.add(_iconLabel, BorderLayout.WEST);
            innerPanel.add(_message, BorderLayout.CENTER);
            getContentPane().add(externalPanel);
        }
 
        /**
         * start the animate
         */
        public void animate() {
            new Animation(this).start();
        }
 
    }
 
    /**
     * animation style
     */
    class Animation extends Thread {
 
        ToolTipSingle _single;
 
        public Animation(ToolTipSingle single) {
            this._single = single;
        }
 
        /**
         * call animate and input position
         *
         * @param posx
         * @param startY
         * @param endY
         * @throws InterruptedException
         */
        private void animateVertically(int posx, int startY, int endY)
                throws InterruptedException {
            _single.setLocation(posx, startY);
            if (endY < startY) {
                for (int i = startY; i > endY; i -= _step) {
                    _single.setLocation(posx, i);
                    Thread.sleep(_stepTime);
                }
            } else {
                for (int i = startY; i < endY; i += _step) {
                    _single.setLocation(posx, i);
                    Thread.sleep(_stepTime);
                }
            }
            _single.setLocation(posx, endY);
        }
 
        /**
         * run the animate
         */
        public void run() {
            try {
                boolean animate = true;
                GraphicsEnvironment ge = GraphicsEnvironment
                        .getLocalGraphicsEnvironment();
                Rectangle screenRect = ge.getMaximumWindowBounds();
                int screenHeight = (int) screenRect.height;
                int startYPosition;
                int stopYPosition;
                if (screenRect.y > 0) {
                    animate = false;
                }
                _maxToolTipSceen = screenHeight / _height;
                int posx = (int) screenRect.width - _width - 1;
                _single.setLocation(posx, screenHeight);
                _single.setVisible(true);
                if (_useTop) {
                    _single.setAlwaysOnTop(true);
                }
                if (animate) {
                    startYPosition = screenHeight;
                    stopYPosition = startYPosition - _height - 1;
                    if (_countOfToolTip > 0) {
                        stopYPosition = stopYPosition
                                - (_maxToolTip % _maxToolTipSceen * _height);
                    } else {
                        _maxToolTip = 0;
                    }
                } else {
                    startYPosition = screenRect.y - _height;
                    stopYPosition = screenRect.y;
 
                    if (_countOfToolTip > 0) {
                        stopYPosition = stopYPosition
                                + (_maxToolTip % _maxToolTipSceen * _height);
                    } else {
                        _maxToolTip = 0;
                    }
                }
 
                _countOfToolTip++;
                _maxToolTip++;
 
                animateVertically(posx, startYPosition, stopYPosition);
                Thread.sleep(_displayTime);
                animateVertically(posx, stopYPosition, startYPosition);
 
                _countOfToolTip--;
                _single.setVisible(false);
                _single.dispose();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
 
    /**
     * Set image and information
     *
     * @param icon
     * @param msg
     */
    public void setToolTip(Icon icon, String msg) {
        ToolTipSingle single = new ToolTipSingle();
        if (icon != null) {
            single._iconLabel.setIcon(icon);
        }
        single._message.setText(msg);
        single.animate();
    }
 
    /**
     * Set the imformation
     *
     * @param msg
     */
    public void setToolTip(String msg) {
        setToolTip(null, msg);
    }
 
    /**
     * Get current font
     *
     * @return
     */
    public Font getMessageFont() {
        return _font;
    }
 
    /**
     *  Set current font
     *
     * @param font
     */
    public void setMessageFont(Font font) {
        _font = font;
    }
 
    /**
     * Get Border Color
     *
     * @return
     */
    public Color getBorderColor() {
        return _border;
    }
 
    /**
     * set Border Color
     *
     * @param _bgColor
     */
    public void setBorderColor(Color borderColor) {
        this._border = borderColor;
    }
 
    /**
     * get Display Time
     *
     * @return
     */
    public int getDisplayTime() {
        return _displayTime;
    }
 
    /**
     * Set display Time
     *
     * @param displayTime
     */
    public void setDisplayTime(int displayTime) {
        this._displayTime = displayTime;
    }
 
    /**
     * 
     *
     * @return
     */
    public int getGap() {
        return _gap;
    }
 
    /**
     * 
     *
     * @param gap
     */
    public void setGap(int gap) {
        this._gap = gap;
    }
 
    /**
     * 
     *
     * @return
     */
    public Color getMessageColor() {
        return _messageColor;
    }
 
    /**
     * 
     *
     * @param messageColor
     */
    public void setMessageColor(Color messageColor) {
        this._messageColor = messageColor;
    }
 
    /**
     * 
     *
     * @return
     */
    public int getStep() {
        return _step;
    }
 
    /**
     * 
     *
     * @param _step
     */
    public void setStep(int _step) {
        this._step = _step;
    }
 
    public int getStepTime() {
        return _stepTime;
    }
 
    public void setStepTime(int _stepTime) {
        this._stepTime = _stepTime;
    }
 
    public Color getBackgroundColor() {
        return _bgColor;
    }
 
    public void setBackgroundColor(Color bgColor) {
        this._bgColor = bgColor;
    }
 
    public int getHeight() {
        return _height;
    }
 
    public void setHeight(int height) {
        this._height = height;
    }
 
    public int getWidth() {
        return _width;
    }
 
    public void setWidth(int width) {
        this._width = width;
    }
 
    public static void main(String[] args) {
 
    	Desktop tip = new Desktop();
    	
        tip.setToolTip(new ImageIcon("img.jpg"),
                "Hi, you got a new notifycation! -- COMP512");

    }
 
}
