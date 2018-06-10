import graphics.Color;
import graphics.Rectangle;
import graphics.Text;

import static org.lwjgl.opengl.GL11.glColor3f;

public class Button implements Positionable {
    private int x, y, width, height;
    private String text;
    private Color color;
    private boolean focused;
    float r,g,b;
    private Text t;

    public Button(int x, int y, int width, int height, String text,float r, float g, float b) {
        focused = false;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.r=r;
        this.g=g;
        this.b=b;
        t=new Text(x+60,y+33,text, 1.0f,1f,1);
    }

    public boolean isFocused(int mx, int my) {
        return focused = (mx > x && mx < x + width && my > y && my < y + height);
    }

    public boolean isFocused() {
        return focused;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void display()
    {
        glColor3f(r, g, b);
        Rectangle.display(x,y,width,height,true);
        t.display();
    }

}
