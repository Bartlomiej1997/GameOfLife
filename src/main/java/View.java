import graphics.*;
import graphics.Color;
import graphics.Input.MouseButtonsHandler;
import graphics.Input.MouseHandler;

import java.util.Vector;

import static org.lwjgl.opengl.GL11.*;

public class View
{
    private Window window;
    private Button buttons;
    private Vector<Shape> shapes;
    private Vector<Checkbox> checkboxesS;
    private Text t;
    public static int gridX = 404, gridY = 34, gridWidth = 1114, gridHeight = 1044, delaySlider = 0;   //zoom w przedziale [0,100] -  ustawiany na sliderze
    public static int rulesX = 1520, rulesY = 2, rulesWidth = 400, rulesHeight = 535;
    private Slider zoomSlider;

    private boolean selection=true;
    private int iSelection=10;
    private int jSelection=10;
    private int widthSelection=11;
    private int heightSelection=11;


    public View()
    {
        window = new Window(1920, 1080, "GOL", false);
        Text.load_font("sansation.ttf");
        //TODO implement me
        shapes = new Vector<Shape>();
        createLayout();
        checkboxesS = new Vector<Checkbox>();
        //createCheckboxes();




        t = new Text(100, 100, "Lubie placki", 1.0f, 0f, 0f);
    }


    private void createLayout()
    {
        shapes.add(new Rectangle(2, 2, 400, 1076));//tools
        shapes.add(new Rectangle(404, 2, 150, 30));//card
        shapes.add(new Rectangle(556, 2, 150, 30));//card
        shapes.add(new Rectangle(708, 2, 150, 30));//card
        shapes.add(new Rectangle(860, 2, 150, 30));//card
        shapes.add(new Rectangle(1012, 2, 150, 30));//card
        shapes.add(new Rectangle(gridX, gridY, gridWidth, gridHeight));//grid
        shapes.add(new Rectangle(rulesX, rulesY, rulesWidth, rulesHeight));//rules
        shapes.add(new Rectangle(1520, 540, 400, 537));//clipboard
    }

    private void displayLayout()
    {
        for (Shape s : shapes)
            window.display(s);
    }

    private void createCheckboxes()
    {
        int x = 35;
        int y = 100;
        for (int i = 1; i < 3; i++)
            for (int j = 1; j < 9; j++)
            {
                //checkboxesS.add(new Checkbox(rulesX + j * x, rulesY + i * y, 25));
            }
    }

    private void displayCheckboxes()
    {


        double mouseX = MouseHandler.xPos();
        double mouseY = MouseHandler.yPos();
        if (mouseX > rulesX && mouseX < (rulesX + rulesWidth) && mouseY > rulesY && mouseY < (rulesY + rulesHeight))
        {
            for (Checkbox c : checkboxesS)
                if (c.isFocused((int) mouseX, (int) mouseY) && MouseButtonsHandler.isKeyDown(0))
                {
                    c.changeState();
                    break;
                }
        }

        for (Checkbox c : checkboxesS)
            c.draw();

    }

    public double[] getMousePosition()
    {
        return window.getMousePosition();
    }

    public void display()
    {
        // TODO display everything here: window.display(something); example below
        //shapes.elementAt(1).setColor(0, 0, 0, 1);
        //shapes.elementAt(2).setColor(0.5f, 0.5f, 0.5f, 0.5f);


        displayLayout();

        //displayCheckboxes();

        //delaySlider = displaySlider();

        //glClear(GL_COLOR_BUFFER_BIT);
        /* select white for all lines  */
        //glColor3f(1.0f, 1.0f, 1.0f);

        /* in 1st row, 3 lines, each with a different stipple  */
        //glEnable(GL_LINE_STIPPLE);

        //glLineStipple(5, (short)(255/2));  /*  dotted  */
        //drawOneLine(0.0f, 125.0f, 500.0f, 500.0f);


        //glDisable(GL_LINE_STIPPLE);
        //glFlush();
        t.display();
        window.update();
        clearScreen();

    }

    private int displaySlider()
    {
        double mouseX = MouseHandler.xPos();
        double mouseY = MouseHandler.yPos();
        if (zoomSlider.isFocused((int) mouseX, (int) mouseY) && MouseButtonsHandler.isKeyDown(0) && !zoomSlider.state())
        {
            zoomSlider.changeState();
        }
        if (zoomSlider.state())
        {
            if (!MouseButtonsHandler.isKeyDown(0)) zoomSlider.changeState();
            zoomSlider.slide((int) mouseX);
        }
        zoomSlider.draw();
        return zoomSlider.getPercent();
    }

    private void displayMask()
    {
        glColor3f(0, 0, 0);
        Rectangle.display(0, 0, gridX, 1080, true);
        Rectangle.display(gridX, 0, gridWidth, gridY, true);
        Rectangle.display(gridX + gridWidth, 0, 1920 - gridX - gridWidth, 1080, true);
        Rectangle.display(gridX, gridY + gridHeight, gridWidth, 1080 - gridY - gridHeight, true);
        glColor3f(1, 1, 1);
    }


    public int display(Grid grid)
    {
        int codedPosition = displayGrid((grid));
        displayMask();
        return codedPosition;
    }


    private int displayGrid(Grid grid)
    {
        int codedPosition = -1;
        int gridType;
        double mouseX = MouseHandler.xPos();
        double mouseY = MouseHandler.yPos();
        float a = grid.getZoom();
        float prevA = grid.getPrevZoom();
        float s = (float) Math.sqrt(3);
        double newX = 0, newY = 0;
        float cellWidth = 0, cellHeight = 0;

        Color lineColor=new Color(0.2f,0.2f,0.2f,1);
        Color fillColor=new Color(0.8f,0.8f,0.8f,1);

        if (grid instanceof Squared)
        {
            gridType = 0;
            a += 2;
            prevA += 2;
            cellWidth = a;
            cellHeight = a;
        } else if (grid instanceof Triangular)
        {
            gridType = 1;
            a += 4;
            prevA += 4;
            cellWidth = a / 2;
            cellHeight = a * s / 2;
        } else if (grid instanceof Hexagonal)
        {
            gridType = 2;
            a += 1;
            prevA += 1;
            cellWidth = a * 1.5f;
            cellHeight = a * s;
        } else return -1;

        int columns = (int) (gridWidth / cellWidth) + 1;
        int rows = (int) (gridHeight / cellHeight) + 1;

        double xoff = grid.getXoff();
        double yoff = grid.getYoff();

        double starti = ((-xoff) / cellWidth);
        double startj = ((-yoff) / cellHeight);

        float x = gridX + (float) (xoff);
        float y = gridY + (float) (yoff);
        if (gridType == 2) y -= cellHeight / 2;

        if (prevA != a)
        {
            double oldX = grid.getOldX();
            double oldY = grid.getOldY();

            if (gridType == 0)
            {
                newX = x + grid.getHighlightedI() * cellWidth;
                newY = y + grid.getHighlightedJ() * cellHeight;
            } else if (gridType == 1)
            {
                newX = x + grid.getHighlightedI() * cellWidth + (grid.getHighlightedJ() % 2) * cellWidth;
                newY = y + grid.getHighlightedJ() * cellHeight;
            } else if (gridType == 2)
            {
                newX = x + grid.getHighlightedI() * cellWidth;
                newY = y + grid.getHighlightedJ() * cellHeight + (grid.getHighlightedI() % 2) * cellHeight / 2;
            }

            grid.setDiffX(oldX - newX); // roznica miedzy starą a nową pozycją podswietlonego
            grid.setDiffY(oldY - newY);

            grid.setPrevZoom(grid.getZoom());
        }

        x += grid.getDiffX();
        y += grid.getDiffY();

        starti -= grid.getDiffX() / cellWidth;
        startj -= grid.getDiffY() / cellHeight;

        if (starti < 0)
        {
            x += starti * cellWidth;
            starti = 0;
            grid.setXoff(-grid.getDiffX());
            //+ wyzerowac jeszcze offset scrolla
        } else if (starti + columns >= Game.GRIDSIZE)
        { // jesli ostatni index komorki przekracza ilosc komorek
            starti = Game.GRIDSIZE - columns;
            x = gridX + (float) -starti * cellWidth;
            grid.setXoff(-(Game.GRIDSIZE - columns) * cellWidth - grid.getDiffX());

        }

        if (startj + rows >= Game.GRIDSIZE)
        {
            startj = Game.GRIDSIZE - rows;
            y = gridY + (float) -startj * cellHeight;
            grid.setYoff(-(Game.GRIDSIZE - rows) * cellHeight - grid.getDiffY());

        } else if (startj < 0)
        {
            y += startj * cellHeight;
            startj = 0;
            grid.setYoff(-grid.getDiffY());
        }
        glColor3f(fillColor.getR(), fillColor.getG(), fillColor.getB());
        if (gridType == 0)
        {
            for (int i = (int) starti; i < columns + starti; i++)
                for (int j = (int) startj; j < rows + startj; j++)
                    if(grid.isCellAlive(i, j)) Rectangle.display(x + i * a, y + j * a, a, a, true);

            glColor3f(lineColor.getR(), lineColor.getG(), lineColor.getB());

            for (int i = (int) starti; i < columns + starti; i++)
                for (int j = (int) startj; j < rows + startj; j++)
                    Rectangle.display(x + i * a, y + j * a, a, a, false);

            if (mouseX > gridX && mouseX < (gridX + gridWidth))
            {
                if (mouseY > gridY && mouseY < (gridY + gridHeight))
                {
                    int i = (int) ((mouseX - x) / a);
                    int j = (int) ((mouseY - y) / a);
                    glColor3f(0, 1, 0);
                    Rectangle.display(x + i * a, y + j * a, a, a, false);
                    codedPosition = Game.GRIDSIZE * i + j;

                    grid.setHighlightedI(i);
                    grid.setHighlightedJ(j);
                    grid.setOldX(x + i * a);
                    grid.setOldY(y + j * a);
                    t.setTxt(i + " " + j);
                    glColor3f(1, 1, 0);

                    if (selection)
                        Rectangle.displaySelected(x, y, a, cellWidth, cellHeight, iSelection,  jSelection, widthSelection, heightSelection);

                }
            }
        } else if (gridType == 1) //triangular
        {
            for (int i = (int) starti; i < columns + starti; i++)
                for (int j = (int) startj; j < rows + startj; j++)
                    if(grid.isCellAlive((i + (j % 2)) % Game.GRIDSIZE, j)) Triangle.display(x + i * cellWidth + (j % 2) * cellWidth, y + j * cellHeight, a, (i % 2) > 0, true);

            glColor3f(lineColor.getR(), lineColor.getG(), lineColor.getB());
            for (int i = (int) starti; i < columns + starti; i++)
                for (int j = (int) startj; j < rows + startj; j++)
                    Triangle.display(x + i * cellWidth + (j % 2) * cellWidth, y + j * cellHeight, a, (i % 2) > 0, false);

            if (mouseX > gridX && mouseX < (gridX + gridWidth))
            {
                if (mouseY > gridY && mouseY < (gridY + gridHeight))
                {
                    int j = (int) ((mouseY - y) / cellHeight);
                    int i = (int) ((mouseX - x - (j % 2.) * cellWidth) / cellWidth);
                    // srodek wybranego trojkata = xM , yyy
                    double xM = x + i * cellWidth + (j % 2.) * cellWidth;
                    double dh = (i % 2) == 1 ? cellHeight / 3 : 2 * cellHeight / 3;
                    double dh2 = (i % 2) == 0 ? cellHeight / 3 : 2 * cellHeight / 3;
                    double yyy = (y + j * cellHeight) + dh;
                    double yyy2 = (y + j * cellHeight) + dh2;
                    // srodek lewego sasiada =p  xL , yyy
                    double xL = xM - a / 2;
                    // srodek prawego sasiada =  xR , yyy
                    double xR = xM + a / 2;

                    double rM = radius(mouseX, mouseY, xM, yyy);
                    // odleglosc myszki od srodka wybranego
                    double rL = radius(mouseX, mouseY, xL, yyy2); // odleglosc myszki od srodka lewego sasiada
                    double rR = radius(mouseX, mouseY, xR, yyy2);// odleglosc myszki od srodka prawego sasiada


                    if (rL < rR)
                    {
                        if (rL < rM)
                            i--;
                    } else if (rR < rM) i++;


                    glColor3f(0, 1, 0);
                    Triangle.display(x + i * cellWidth + (j % 2) * cellWidth, y + j * cellHeight, a, (i % 2) > 0, false/*grid.isCellAlive((i + (j % 2)) % Game.GRIDSIZE, j)*/);
                    codedPosition = Game.GRIDSIZE * (i + j % 2) + j;

                    grid.setHighlightedI(i);
                    grid.setHighlightedJ(j);
                    grid.setOldX(x + i * cellWidth + (j % 2) * cellWidth);
                    grid.setOldY(y + j * cellHeight);
                    t.setTxt(i + " " + j);
                    glColor3f(1, 1, 0);
                    if (selection)
                        Triangle.displaySelected(x, y, a, cellWidth, cellHeight, iSelection,  jSelection, widthSelection, heightSelection);

                }
            }

        } else if (gridType == 2) //hex
        {
            for (int i = (int) starti; i < columns + starti; i++)
                for (int j = (int) startj; j < rows + startj; j++)
                    if (grid.isCellAlive(i, j))
                        Hexagon.display(x + i * cellWidth, y + j * cellHeight + (i % 2) * cellHeight / 2, a, true);

            glColor3f(lineColor.getR(), lineColor.getG(), lineColor.getB());
            for (int i = (int) starti; i < columns + starti; i++)
                for (int j = (int) startj; j < rows + startj; j++)
                    Hexagon.display(x + i * cellWidth, y + j * cellHeight + (i % 2) * cellHeight / 2, a, false);

            if (mouseX > gridX && mouseX < (gridX + gridWidth))
            {
                if (mouseY > gridY && mouseY < (gridY + gridHeight))
                {
                    int i = (int) ((mouseX - x) / cellWidth);
                    int j = (int) ((mouseY - y - (i % 2) * cellHeight / 2) / cellHeight);

                    double xM = x + i * cellWidth + a / 2;
                    double xxxR = xM + cellWidth;
                    double yyy = y + j * cellHeight + (i % 2) * cellHeight / 2 + a * Math.sqrt(3) / 2;
                    double yUS = yyy - cellHeight / 2;
                    double yDS = yyy + cellHeight / 2;
                    double rM = radius(mouseX, mouseY, xM, yyy);
                    double rRDS = radius(mouseX, mouseY, xxxR, yDS);
                    double rRUS = radius(mouseX, mouseY, xxxR, yUS);
                    double min = Math.min(Math.min(rRDS, rRUS), rM);

                    if (rM != min)
                    {
                        if (rRDS == min)
                        {
                            int t = i % 2;
                            int temp = (j + i) % 2;
                            j += (j + i) % 2;
                            i++;
                            if (temp == 0)
                            {
                                j++;
                            }
                            if (t == 0)
                            {
                                j--;
                            }

                        } else if (rRUS == min)
                        {
                            int t = i % 2;
                            int temp = (j + i) % 2;
                            j -= (j + i) % 2;
                            i++;
                            if (temp == 1)
                            {
                                j++;
                            }
                            if (t == 0)
                            {
                                j--;
                            }
                        }
                    }

                    glColor3f(0, 1, 0);
                    Hexagon.display(x + i * cellWidth, y + j * cellHeight + (i % 2) * cellHeight / 2, a, false);
                    codedPosition = Game.GRIDSIZE * i + j;

                    grid.setHighlightedI(i);
                    grid.setHighlightedJ(j);
                    grid.setOldX(x + i * cellWidth);
                    grid.setOldY(y + j * cellHeight + (i % 2) * cellHeight / 2);
                    t.setTxt(i + " " + j);


                    glColor3f(1, 1, 0);

                    if (selection)
                        Hexagon.displaySelected(x, y, a, cellWidth, cellHeight, iSelection,  jSelection, widthSelection, heightSelection);
                }
            }

        }
        glColor3f(1,1,1);
        return codedPosition;
    }




/*

    private int displayTriangular(Grid grid)
    {
        double mouseX = MouseHandler.xPos();
        double mouseY = MouseHandler.yPos();
        int codedPosition = -1;
        float a = 4 + grid.getZoom();
        float prevA = 4 + grid.getPrevZoom();
        float s = (float) Math.sqrt(3);

        float cellWidth = a / 2;
        float cellHeight = a * s / 2;

        int columns = (int) (gridWidth / cellWidth) + 1;
        int rows = (int) (gridHeight / cellHeight) + 1;


        double xoff = grid.getXoff();
        double yoff = grid.getYoff();

        double starti = ((-xoff) / cellWidth);
        double startj = ((-yoff) / cellHeight);



        float x = gridX + (float) (xoff);
        float y = gridY + (float) (yoff);


        if (prevA != a)
        {
            double oldX = grid.getOldX();
            double oldY = grid.getOldY();

            double newX = x + grid.getHighlightedI() * cellWidth + (grid.getHighlightedJ() % 2) * cellWidth;
            double newY = y + grid.getHighlightedJ() * cellHeight;

            grid.setDiffX(oldX - newX); // roznica miedzy starą a nową pozycją podswietlonego
            grid.setDiffY(oldY - newY);

            grid.setPrevZoom(a - 4);

        }


        x += grid.getDiffX();
        y += grid.getDiffY();

        starti -= grid.getDiffX() / cellWidth;
        startj -= grid.getDiffY() / cellHeight;

        if (starti < 0)
        {
            x += starti * cellWidth;
            starti = 0;
            grid.setXoff(-grid.getDiffX());
            //+ wyzerowac jeszcze offset scrolla
        } else if (starti + columns >= Game.GRIDSIZE)
        { // jesli ostatni index komorki przekracza ilosc komorek
            starti = Game.GRIDSIZE - columns;
            x = gridX + (float) -starti * cellWidth;
            grid.setXoff(-(Game.GRIDSIZE - columns)*cellWidth-grid.getDiffX());
        }

        if (startj + rows >= Game.GRIDSIZE)
        {
            startj = Game.GRIDSIZE - rows;
            y = gridY + (float) -startj * cellHeight;
            grid.setYoff(-(Game.GRIDSIZE - rows)*cellHeight-grid.getDiffY());
        } else if (startj < 0)
        {
            y += startj * cellHeight;
            startj = 0;
            grid.setYoff(-grid.getDiffY());
        }

        for (int i = (int) starti; i < columns + starti; i++)
            for (int j = (int) startj; j < rows + startj; j++)
                Triangle.display(x + i * cellWidth + (j % 2) * cellWidth, y + j * cellHeight, a, (i % 2) > 0, grid.isCellAlive((i + (j % 2)) % Game.GRIDSIZE, j));


        if (mouseX > gridX && mouseX < (gridX + gridWidth))
        {
            if (mouseY > gridY && mouseY < (gridY + gridHeight))
            {
                int j = (int) ((mouseY - y) / cellHeight);
                int i = (int) ((mouseX - x - (j % 2.) * cellWidth) /cellWidth);
                // srodek wybranego trojkata = xM , yyy
                double xM = x + i *cellWidth + (j % 2.) * cellWidth;
                double dh = (i % 2) == 1 ? cellHeight/3 : 2*cellHeight/3;
                double dh2 = (i % 2) == 0 ? cellHeight/3 : 2*cellHeight/3;
                double yyy = (y + j * cellHeight) + dh;
                double yyy2 = (y + j * cellHeight) + dh2;
                // srodek lewego sasiada =p  xL , yyy
                double xL = xM - a / 2;
                // srodek prawego sasiada =  xR , yyy
                double xR = xM + a / 2;

                double rM = radius(mouseX, mouseY, xM, yyy);
                // odleglosc myszki od srodka wybranego
                double rL = radius(mouseX, mouseY, xL, yyy2); // odleglosc myszki od srodka lewego sasiada
                double rR = radius(mouseX, mouseY, xR, yyy2);// odleglosc myszki od srodka prawego sasiada


                if (rL < rR)
                {
                    if (rL < rM)
                        i--;
                } else if (rR < rM) i++;


                glColor3f(0, 1, 0);
                Triangle.display(x + i * cellWidth + (j % 2) * cellWidth, y + j * cellHeight, a, (i % 2) > 0, false*/
/*grid.isCellAlive((i + (j % 2)) % Game.GRIDSIZE, j)*//*
);
                codedPosition = Game.GRIDSIZE * (i + j % 2) + j;

                grid.setHighlightedI(i);
                grid.setHighlightedJ(j);
                grid.setOldX(x + i * cellWidth + (j % 2) * cellWidth);
                grid.setOldY( y + j * cellHeight);
                t.setTxt(i + " " + j);
            }
        }
        return codedPosition;
    }



    private int displayHexagonal(Grid grid)
    {
        double mouseX = MouseHandler.xPos();
        double mouseY = MouseHandler.yPos();
        int codedPosition = -1;
        double xoff = grid.getXoff();
        double yoff = grid.getYoff();
        float a = 1 + grid.getZoom();
        float prevA = 1 + grid.getPrevZoom();
        float s = (float) Math.sqrt(3);

        float cellWidth = a * 1.5f;
        float cellHeight = a * s;

        int columns = (int) (gridWidth / cellWidth) + 1;
        int rows = (int) (gridHeight / cellHeight) + 1;

        double starti = ((-xoff) / cellWidth);
        double startj = ((-yoff) / cellHeight);

        if (starti + columns >= Game.GRIDSIZE - 1)
        {
            starti = Game.GRIDSIZE - columns;
            xoff = -starti * cellWidth;
        }
        if (startj + rows >= Game.GRIDSIZE - 1)
        {
            startj = Game.GRIDSIZE - rows;
            yoff = -startj * cellHeight;
        }

        float x = gridX + (float) (xoff);
        float y = gridY + (float) (yoff - cellHeight / 2);

        if (prevA != a)
        {
            double oldX = grid.getOldX();
            double oldY = grid.getOldY();


            double newX = x + grid.getHighlightedI() * cellWidth;
            double newY = y + grid.getHighlightedJ() * cellHeight + (grid.getHighlightedI() % 2) * cellHeight / 2;

            grid.setDiffX(oldX - newX);
            grid.setDiffY(oldY - newY);

            grid.setPrevZoom(a - 1);

        }

        x += grid.getDiffX();
        y += grid.getDiffY();

        starti -= grid.getDiffX() / cellWidth;
        startj -= grid.getDiffY() / cellHeight;

        if (starti < 0)
        {
            x += starti * cellWidth;
            starti = 0;
            grid.setXoff(-grid.getDiffX());
            //+ wyzerowac jeszcze offset scrolla
        } else if (starti + columns >= Game.GRIDSIZE)
        { // jesli ostatni index komorki przekracza ilosc komorek
            starti = Game.GRIDSIZE - columns;
            x = gridX + (float) -starti * cellWidth;
            grid.setXoff(-(Game.GRIDSIZE - columns)*cellWidth-grid.getDiffX());

        }

        if (startj + rows >= Game.GRIDSIZE)
        {
            startj = Game.GRIDSIZE - rows;
            y = gridY + (float) -startj * cellHeight;
            grid.setYoff(-(Game.GRIDSIZE - rows)*cellHeight-grid.getDiffY());

        } else if (startj < 0)
        {
            y += startj * cellHeight;
            startj = 0;
            grid.setYoff(-grid.getDiffY());
        }


        for (int i = (int) starti; i < columns + starti; i++)
            for (int j = (int) startj; j < rows + startj; j++)
                Hexagon.display(x + i * cellWidth, y + j * cellHeight + (i % 2) * cellHeight / 2, a, grid.isCellAlive(i, j));

        if (mouseX > gridX && mouseX < (gridX + gridWidth))
        {
            if (mouseY > gridY && mouseY < (gridY + gridHeight))
            {
                int i = (int) ((mouseX - x) / cellWidth);
                int j = (int) ((mouseY - y - (i % 2) * cellHeight / 2) / cellHeight);

                double xM = x + i * cellWidth + a / 2;
                double xxxR = xM + cellWidth;
                double yyy = y + j * cellHeight + (i % 2) * cellHeight / 2 + a * Math.sqrt(3) / 2;
                double yUS = yyy - cellHeight / 2;
                double yDS = yyy + cellHeight / 2;
                double rM = radius(mouseX, mouseY, xM, yyy);
                double rRDS = radius(mouseX, mouseY, xxxR, yDS);
                double rRUS = radius(mouseX, mouseY, xxxR, yUS);
                double min = Math.min(Math.min(rRDS, rRUS), rM);

                if (rM != min)
                {
                    if (rRDS == min)
                    {
                        int t = i % 2;
                        int temp = (j + i) % 2;
                        j += (j + i) % 2;
                        i++;
                        if (temp == 0)
                        {
                            j++;
                        }
                        if (t == 0)
                        {
                            j--;
                        }

                    } else if (rRUS == min)
                    {
                        int t = i % 2;
                        int temp = (j + i) % 2;
                        j -= (j + i) % 2;
                        i++;
                        if (temp == 1)
                        {
                            j++;
                        }
                        if (t == 0)
                        {
                            j--;
                        }
                    }
                }

                glColor3f(0, 1, 0);
                Hexagon.display(x + i * cellWidth, y + j * cellHeight + (i % 2) * cellHeight / 2, a, false);
                codedPosition = Game.GRIDSIZE * i + j;

                grid.setHighlightedI(i);
                grid.setHighlightedJ(j);
                grid.setOldX(x + i * cellWidth);
                grid.setOldY(y + j * cellHeight + (i % 2) * cellHeight / 2);
                t.setTxt(i + " " + j);

            }
        }
        return codedPosition;
    }


    private int displaySquared(Grid grid)
    {
        double mouseX = MouseHandler.xPos();
        double mouseY = MouseHandler.yPos();
        int codedPosition = -1;
        float a = 2 + grid.getZoom();
        float prevA = 2 + grid.getPrevZoom();

        int columns = (int) (gridWidth / a)+1;
        int rows = (int) (gridHeight / a)+1 ;

        double xoff=grid.getXoff();
        double yoff=grid.getYoff();

        double starti = -xoff / a;
        double startj = -yoff / a;

        float x = gridX + (float) (xoff);
        float y = gridY + (float) (yoff);

        if(prevA != a)
        {
            double oldX = grid.getOldX();
            double oldY = grid.getOldY();

            double newX = x + grid.getHighlightedI() * a;
            double newY = y + grid.getHighlightedJ() * a;

            grid.setDiffX(oldX - newX);
            grid.setDiffY(oldY - newY);

            grid.setPrevZoom(a-2);
        }

        xoff+=grid.getDiffX();
        yoff+=grid.getDiffY();

        starti-=grid.getDiffX()/a;
        startj-=grid.getDiffY()/a;

        if(starti<0)
        {

            xoff+=starti*a;
            starti=0;
            grid.setXoff(-grid.getDiffX());

        }
        else if (starti + columns >= Game.GRIDSIZE)
        {
            starti = Game.GRIDSIZE - columns;
            xoff = (float)-starti * a;
            grid.setXoff(-(Game.GRIDSIZE-columns)*a-grid.getDiffX());

        }

        if (startj + rows >= Game.GRIDSIZE)
        {
            startj = Game.GRIDSIZE - rows;
            yoff = (float)-startj * a;
            grid.setYoff(-(Game.GRIDSIZE-rows)*a-grid.getDiffY());
        } else if(startj<0)
        {
            yoff += startj * a;
            startj = 0;
            grid.setYoff(-grid.getDiffY());

        }


        x = gridX + (float) (xoff);
        y = gridY + (float) (yoff);

        for (int i = (int) starti; i < columns + starti; i++)
            for (int j = (int) startj; j < rows + startj; j++)
                Rectangle.display(x + i * a, y + j * a, a, a, grid.isCellAlive(i, j));

        if (mouseX > gridX && mouseX < (gridX + gridWidth)) {
            if (mouseY > gridY && mouseY < (gridY + gridHeight)) {
                int i = (int) ((mouseX - x) / a);
                int j = (int) ((mouseY - y) / a);
                glColor3f(0, 1, 0);
                Rectangle.display(x + i * a, y + j * a, a, a, false);
                codedPosition = Game.GRIDSIZE * i + j;

                grid.setHighlightedI(i);
                grid.setHighlightedJ(j);
                grid.setOldX(x + i * a);
                grid.setOldY(y + j * a);
                t.setTxt(i + " " + j);
            }
        }
        return codedPosition;
    }

*/

    private double radius(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }



    public boolean shouldRun() {
        return window.isOpen();
    }

    public void closeWindow() {
        window.close();
    }

    public void clearScreen() {
        window.clear();
    }

}