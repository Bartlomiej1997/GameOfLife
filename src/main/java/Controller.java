import static org.lwjgl.glfw.GLFW.*;

import graphics.Input.*;

import java.util.ArrayList;

public class Controller {
    private View view;
    private Model model;
    private int codedPos = -1;
    private ArrayList<Button> cardButtons;
    private ArrayList<Button> cardDelButtons;
    private Checkbox[][] rulesCheckboxes;
    private Slider delaySlider;
    private Selection selection;
    private Toolset toolset;
    private ClipLib clips;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        clips = new ClipLib("clips.txt");
        toolset = new Toolset();
        selection = new Selection(0, 0, 1, 1);
        cardButtons = new ArrayList<>();
        cardDelButtons = new ArrayList<>();
        rulesCheckboxes = new Checkbox[2][13];
        delaySlider = new Slider(view.previewX + 50, view.previewY + 400, 300, 10);
        initButtons();
        initCheckboxes();
    }

    public void run() {

        while (view.shouldRun()) {
            handleEvents();
            update();
            display();
        }
    }

    private void update() {
        model.update();
    }

    private void display() {
        codedPos = view.display(model.getGridValues(), selection, model.getClipboard());

        for (Checkbox[] checkbox : rulesCheckboxes)
            for (int i = 0; i < model.getRuleSize(); i++)
                checkbox[i].draw();
        for (Button button : cardButtons) button.display();
        for (Button button : cardDelButtons) button.display();
        toolset.display();
        delaySlider.draw();
        clips.displayButtons();
        view.display();
    }

    private void handleEvents() {
        switch (view.getState()) {
            case clipboard:

                break;
            case grid:

                break;

            case cards:

                break;

            case tools:

                break;

            case preview:

                break;
        }
        handleSliders();
        handleButtons();
        toolset.handleTools();
        handleCheckboxes();
        handleSelection();
        clips.handleButtons();
        if (KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE)) view.closeWindow();
        if (KeyboardHandler.isKeyClicked(GLFW_KEY_SPACE)) model.randomize();
        if (KeyboardHandler.isKeyClicked(GLFW_KEY_ENTER)) model.update();
        if (KeyboardHandler.isKeyClicked(GLFW_KEY_D)) model.nextCard();
        if (KeyboardHandler.isKeyClicked(GLFW_KEY_A)) model.prevCard();
        if (KeyboardHandler.isKeyClicked(GLFW_KEY_P)) model.pause();
        if (KeyboardHandler.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            if (KeyboardHandler.isKeyClicked(GLFW_KEY_1))
                addCard(gridType.Squared);
            if (KeyboardHandler.isKeyClicked(GLFW_KEY_2))
                addCard(gridType.Triangular);
            if (KeyboardHandler.isKeyClicked(GLFW_KEY_3))
                addCard(gridType.Hexagonal);
        }
        if (MouseButtonsHandler.isKeyDown(GLFW_MOUSE_BUTTON_LEFT))
            if (codedPos != -1) model.draw(codedPos, true);
        if (MouseButtonsHandler.isKeyDown(GLFW_MOUSE_BUTTON_RIGHT))
            if (codedPos != -1) model.draw(codedPos, false);

        model.incZoom((int) ScrollHandler.wheelMovement());

        //   model.
        if (MouseButtonsHandler.isKeyDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            model.moveGrid(MouseHandler.xRel(), MouseHandler.yRel());
        }

        if (KeyboardHandler.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            if (KeyboardHandler.isKeyClicked(GLFW_KEY_L)) {
                clips.loadFromFile("clips.txt");
            }
            if (KeyboardHandler.isKeyClicked(GLFW_KEY_S)) {
                clips.saveToFile("clips.txt");
            }
            if (KeyboardHandler.isKeyClicked(GLFW_KEY_A)) {
                int i = clips.size();
                clips.addClip(model.getClipboard(), () -> model.setClipboard(clips.getClipboard(i)), () -> System.out.println("delete"));
            }
            if (KeyboardHandler.isKeyClicked(GLFW_KEY_G)) {
                model.setClipboard(clips.getClipboard(0));
            }
        }


        KeyboardHandler.clear();
        MouseButtonsHandler.clear();
        ScrollHandler.clear();
        MouseHandler.clear();
    }

    private void handleSelection() {
        if (codedPos != -1) {
            if (KeyboardHandler.isKeyClicked(GLFW_KEY_LEFT_SHIFT))
                selection.setXY(codedPos);
            if (KeyboardHandler.isKeyDown(GLFW_KEY_LEFT_SHIFT))
                selection.setWH(codedPos);
        }
        if (KeyboardHandler.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            if (KeyboardHandler.isKeyClicked(GLFW_KEY_C))
                if (selection.isSelected()) model.setClipboard(selection.getClipboard(model.getGridValues()));
            if (KeyboardHandler.isKeyClicked(GLFW_KEY_V))
                model.pasteClipboard((codedPos - (codedPos % Game.GRIDSIZE)) / Game.GRIDSIZE, codedPos % Game.GRIDSIZE);
        }


    }


    private void handleSliders() {
        if (delaySlider.isFocused((int) MouseHandler.xPos(), (int) MouseHandler.yPos()) && MouseButtonsHandler.isKeyDown(0) && !delaySlider.state()) {
            delaySlider.changeState();
        }
        if (delaySlider.state()) {
            if (!MouseButtonsHandler.isKeyDown(0)) delaySlider.changeState();
            delaySlider.slide((int) MouseHandler.xPos());
            model.setDelay(delaySlider.getPercent() * 0.005);
        }

    }

    private void initButtons() {
        addCardButton(404, 2, 150, 30, model.getGridValues().getClass().getName(), () -> model.setCardIndex(0), () -> {
            model.delCard(0);
            cardButtons.remove(0);
            cardDelButtons.remove(0);
        });
    }

    private void initCheckboxes() {
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 13; j++) {
                int x = i;
                int y = j;
                rulesCheckboxes[i][j] = new Checkbox(view.gridX + view.gridWidth + 20 + 40 * j, view.gridY + 50 + i * 40, 25, (state) -> model.setRule(x, y, state));
            }
        setRulesCheckboxes();
    }

    private void setRulesCheckboxes() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < model.getRuleSize(); j++) {
                rulesCheckboxes[i][j].setState(model.getRule(i, j));
            }
        }
    }

    private void setDelaySlider() {
        delaySlider.setPercent((int) (model.getDelay() / 0.005));
    }

    private void addCardButton(int x, int y, int width, int height, String text, ButtonHandler copyHandle, ButtonHandler delHandle) {
        cardButtons.add(new Button(x, y, (int) (0.8 * width), height, text, copyHandle, 0.5f, 0.5f, 0.5f));
        cardDelButtons.add(new Button(x + (int) (0.8 * width), y, (int) (0.2 * width), height, "x", delHandle, 0.8f, 0, 0));
    }

    private void handleButtons() {
        if (MouseButtonsHandler.isKeyClicked(GLFW_MOUSE_BUTTON_LEFT)) {
            for (Button button : cardButtons) {
                if (button.isFocused((int) MouseHandler.xPos(), (int) MouseHandler.yPos())) {
                    button.press();
                    setRulesCheckboxes();
                    setDelaySlider();
                    break;
                }
            }
            for (Button button : cardDelButtons) {
                if (button.isFocused((int) MouseHandler.xPos(), (int) MouseHandler.yPos())) {
                    button.press();
                    setRulesCheckboxes();
                    setDelaySlider();
                    break;
                }
            }
        }
    }

    private void handleCheckboxes() {
        if (MouseButtonsHandler.isKeyClicked(GLFW_MOUSE_BUTTON_LEFT)) {
            for (Checkbox[] checkbox : rulesCheckboxes)
                for (int i = 0; i < model.getRuleSize(); i++) {
                    if (checkbox[i].isFocused((int) MouseHandler.xPos(), (int) MouseHandler.yPos())) {
                        checkbox[i].press();
                    }
                }
        }
    }

    private void addCard(gridType type) {

        model.addCard(type);
        int n = model.getCardsAmount() - 1;
        addCardButton(
                Area.cards.getX() + 152 * n,
                Area.cards.getY(),
                150,
                Area.cards.getHeight(),
                model.getGridValues().getClass().getName(),
                () -> model.setCardIndex(n),
                () -> {
                    cardButtons.remove(n);
                    cardDelButtons.remove(n);
                    model.delCard(n);
                    resetCardButtons();
                }
        );
        setRulesCheckboxes();
        setDelaySlider();
    }

    private void resetCardButtons() {
        for (int i = 0; i < cardButtons.size(); i++) {
            int I = i;
            cardButtons.get(i).setPosition(Area.cards.getX() + 152 * i, Area.cards.getY());
            cardDelButtons.get(i).setPosition(Area.cards.getX() + 152 * i + (int) (0.8 * 152), Area.cards.getY());
            cardButtons.get(i).setHandler(() -> model.setCardIndex(I));
            cardDelButtons.get(i).setHandler(
                    () -> {
                        cardButtons.remove(I);
                        cardDelButtons.remove(I);
                        model.delCard(I);
                        resetCardButtons();
                    });
        }
    }
}