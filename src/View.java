import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.converter.DefaultStringConverter;

public class View extends BorderPane {
    private final Controller controller;

    private StackPane imagePane;
    private ImageView imageView;
    private Button nextButton;
    private Button previousButton;
    private Slider brightnessSlider;
    private Slider zoomSlider;
    private SelectionRectangle selectionRectangle;
    private VBox topBox;
    private ToolBar settingsToolBar;
    private Label brightnessLabel;
    private ToolBar navBar;
    private VBox selectionSidePanel;

    private MenuItem fileOpenFolderItem;
    private MenuItem viewFitWindowItem;
    private CheckMenuItem viewShowSettingsItem;
    //private MenuItem viewBoundingBoxColorItem;
    private MenuItem fileSaveItem;

    private DragAnchor mousePressed = new DragAnchor();

    private TableView<BoundingBoxItem> boundingBoxItemTableView;
    private TextField nameInput;
    private ColorPicker boundingBoxColorPicker;
    private Button addButton;
    // Maybe replace with enums
    private static final String NEXT_ICON_PATH = "icons/arrow_right.png";
    private static final String PREVIOUS_ICON_PATH = "icons/arrow_left.png";
    private static final String ZOOM_ICON_PATH = "icons/zoom.png";
    private static final String BRIGHTNESS_ICON_PATH = "icons/brightness.png";
    private static final String DELETE_ICON_PATH = "icons/delete.png";
    private static final String TOP_BOX_STYLE = "topBox";
    private static final String SETTINGS_BOX_STYLE = "settingsBox";
    private static final String IMAGE_PANE_STYLE = "pane";
    private static final String SIDE_PANEL_STYLE = "side-panel";
    private static final String FILE_MENU_TEXT = "_File";
    private static final String VIEW_MENU_TEXT = "_View";
    private static final String OPEN_FOLDER_TEXT = "_Open Folder...";
    private static final String SAVE_TEXT = "_Save...";
    private static final String FIT_WINDOW_TEXT = "_Fit Window";
    private static final String BOUNDING_BOX_COLOR_TEXT = "Bounding Box Color";
    private static final String SHOW_SETTINGS_BAR_TEXT = "Settings Bar";
    private static final double ICON_WIDTH = 20.0;
    private static final double ICON_HEIGHT = 20.0;
    private static final double IMAGE_PADDING = 30.0;



    public View(Controller controller) {
        this.controller = controller;
        imagePane = createImagePane();
        topBox = createTopBox();
        settingsToolBar = createSettingsBar();
        selectionSidePanel = createSelectionSidePanel();

        this.setTop(topBox);
        this.setCenter(imagePane);
        this.setRight(settingsToolBar);
        this.setLeft(selectionSidePanel);
        setActionsFromController();
        setInternalBindingsAndListeners();
    }

    private StackPane createImagePane() {
        StackPane imagePane = new StackPane();
        imagePane.getStyleClass().add(IMAGE_PANE_STYLE);
        selectionRectangle = new SelectionRectangle();
        imageView = new ImageView();
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setPickOnBounds(true);

        imagePane.getChildren().add(imageView);
        imagePane.getChildren().addAll(selectionRectangle.getNodes());

        return imagePane;
    }

    private VBox createTopBox() {
        VBox topBox = new VBox();

        navBar = createToolBar();

        topBox.getChildren().addAll(createMenuBar(), new Separator(), navBar);
        topBox.getStyleClass().add(TOP_BOX_STYLE);

        return topBox;
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu(FILE_MENU_TEXT);
        Menu viewMenu = new Menu(VIEW_MENU_TEXT);

        fileOpenFolderItem = new MenuItem(OPEN_FOLDER_TEXT);

        viewFitWindowItem = new MenuItem(FIT_WINDOW_TEXT);
        viewShowSettingsItem = new CheckMenuItem(SHOW_SETTINGS_BAR_TEXT);
        //boundingBoxColorPicker = new ColorPicker();
        //viewBoundingBoxColorItem = new MenuItem(BOUNDING_BOX_COLOR_TEXT, boundingBoxColorPicker);
        fileSaveItem = new MenuItem(SAVE_TEXT);

        fileMenu.getItems().addAll(fileOpenFolderItem, fileSaveItem);
        viewMenu.getItems().addAll(viewFitWindowItem, viewShowSettingsItem);

        menuBar.getMenus().addAll(fileMenu, viewMenu);

        return menuBar;
    }

    private ToolBar createToolBar() {
        ToolBar toolBar = new ToolBar();

        Pane leftSpace = new Pane();
        Pane rightSpace = new Pane();

        HBox.setHgrow(leftSpace, Priority.ALWAYS);
        HBox.setHgrow(rightSpace, Priority.ALWAYS);

        //zoomSlider = new Slider(1, 1.5, 1);
        //Label zoomLabel = createIconLabel(ZOOM_ICON_PATH);

//        brightnessSlider = new Slider(-0.5, 0.5, 0);
//        Label brightnessLabel = createIconLabel(BRIGHTNESS_ICON_PATH);

        nextButton = createIconButton(NEXT_ICON_PATH);
        nextButton.setFocusTraversable(false);

        previousButton = createIconButton(PREVIOUS_ICON_PATH);
        previousButton.setFocusTraversable(false);

//        toolBar.getItems().addAll(zoomLabel, zoomSlider,
//                leftSpace, previousButton, nextButton, rightSpace, brightnessLabel, brightnessSlider);
        toolBar.getItems().addAll(leftSpace, previousButton, nextButton, rightSpace);
        toolBar.setVisible(false);

        return toolBar;
    }

    private Button createIconButton(String iconPath) {
        Button button = new Button();
        ImageView iconView = new ImageView(getClass().getResource(iconPath).toString());

        iconView.setFitWidth(ICON_WIDTH);
        iconView.setFitHeight(ICON_HEIGHT);
        iconView.setPreserveRatio(true);
        button.setGraphic(iconView);

        return button;
    }

    private Label createIconLabel(String iconPath) {
        Label label = new Label();
        ImageView iconView = new ImageView(getClass().getResource(iconPath).toString());

        iconView.setFitWidth(ICON_WIDTH);
        iconView.setFitHeight(ICON_HEIGHT);
        iconView.setPreserveRatio(true);
        label.setGraphic(iconView);

        return label;
    }

    private ToolBar createSettingsBar() {
        ToolBar settingsBar = new ToolBar();
        settingsBar.setOrientation(Orientation.VERTICAL);
        settingsBar.getStyleClass().add(SETTINGS_BOX_STYLE);
        zoomSlider = new Slider(1, 1.5, 1);
        Label imageSettings = new Label("Image");
        Label zoomLabel = createIconLabel(ZOOM_ICON_PATH);

        HBox zoomHBox = new HBox(zoomLabel, zoomSlider);
        zoomHBox.setPadding(new Insets(20, 0, 20, 0));
        zoomHBox.setSpacing(10);

        brightnessSlider = new Slider(-0.5, 0.5, 0);
        brightnessLabel = createIconLabel(BRIGHTNESS_ICON_PATH);

        HBox brightnessHBox = new HBox(brightnessLabel, brightnessSlider);
        brightnessHBox.setPadding(new Insets(20, 0, 20, 0));
        brightnessHBox.setSpacing(10);

//        Label colorLabel = new Label("Bounding Box");
//        boundingBoxColorPicker = new ColorPicker();
//
//        HBox colorHBox = new HBox(colorLabel, boundingBoxColorPicker);
//        colorHBox.setPadding(new Insets(10, 0, 10, 0));
//        boundingBoxColorPicker.getStyleClass().add("button");
        //colorHBox.setSpacing(10);

        settingsBar.getItems().addAll(new Separator(), imageSettings, zoomHBox, brightnessHBox, new Separator());
        settingsBar.setPadding(new Insets(0, 15, 0, 15));
        return settingsBar;
    }

    private VBox createSelectionSidePanel(){
        VBox sidePanel = new VBox();

        boundingBoxItemTableView = createBoundingBoxTableView();

        nameInput = new TextField();
        nameInput.setMaxWidth(100);

        boundingBoxColorPicker = new ColorPicker();
        boundingBoxColorPicker.setMaxWidth(50);
        boundingBoxColorPicker.setMaxHeight(25);
        //boundingBoxColorPicker.getStyleClass().add("split-button");

        Pane leftSpace = new Pane();
        Pane rightSpace = new Pane();

        HBox.setHgrow(leftSpace, Priority.ALWAYS);
        HBox.setHgrow(rightSpace, Priority.ALWAYS);

        addButton = new Button("Add");
        addButton.setFocusTraversable(false);
        HBox addItemControls = new HBox(nameInput, leftSpace, boundingBoxColorPicker, rightSpace, addButton);
        addItemControls.getStyleClass().add("table-view-input-controls");

        sidePanel.getChildren().addAll(boundingBoxItemTableView, addItemControls);
        sidePanel.setSpacing(5);


        sidePanel.getStyleClass().add(SIDE_PANEL_STYLE);

        return sidePanel;
    }

    private TableView<BoundingBoxItem> createBoundingBoxTableView(){
        TableView<BoundingBoxItem> tableView = new TableView<>();
        tableView.setEditable(true);

        TableColumn<BoundingBoxItem, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(165);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setEditable(true);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<BoundingBoxItem, Color> colorColumn = new TableColumn<>("Color");
        colorColumn.setMinWidth(5);
        colorColumn.setMaxWidth(5);
        colorColumn.setCellFactory(factory -> new ColorTableCell());

        TableColumn<BoundingBoxItem, BoundingBoxItem> deleteColumn = new TableColumn<>("Delete");
        deleteColumn.setMinWidth(20);
        deleteColumn.setMaxWidth(20);
        deleteColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        deleteColumn.setCellFactory(value -> new TableCell<>() {
            private final Button deleteButton = new Button();
            private final Region deleteIcon = new Region();

            @Override
            protected void updateItem(BoundingBoxItem item, boolean empty){
                super.updateItem(item, empty);
                if(item == null){
                    setGraphic(null);
                    return;
                }

                setGraphic(deleteButton);
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setPickOnBounds(true);
                deleteIcon.getStyleClass().add("icon");
                deleteButton.setGraphic(deleteIcon);
                deleteButton.setFocusTraversable(false);
                deleteButton.setOnAction(event -> getTableView().getItems().remove(item));
            }
        });


        tableView.getColumns().addAll(colorColumn, nameColumn, deleteColumn);
        tableView.setMaxWidth(200);
        tableView.setMaxHeight(300);
        tableView.getStyleClass().add("noheader");

        return tableView;
    }


    public MenuItem getFileOpenFolderItem() {
        return fileOpenFolderItem;
    }

    public MenuItem getViewFitWindowItem() {
        return viewFitWindowItem;
    }

    public MenuItem getFileSaveItem() {
        return fileSaveItem;
    }

    public Button getPreviousButton() {
        return previousButton;
    }

    public Button getNextButton() {
        return nextButton;
    }

    public SelectionRectangle getSelectionRectangle() {
        return selectionRectangle;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(Image image) {

        imageView.setImage(image);
        imageView.setPreserveRatio(true);
//        imageView.setViewport(new Rectangle2D(0,0,image.getWidth(), image.getHeight()));

        setInitialImageViewSize();
    }

    private void setInitialImageViewSize() {
        double imageWidth = imageView.getImage().getWidth();
        double imageHeight = imageView.getImage().getHeight();
        double maxAllowedWidth = imagePane.getWidth() - 2 * IMAGE_PADDING;
        double maxAllowedHeight = imagePane.getHeight() - 2 * IMAGE_PADDING;

        imageView.setFitWidth(Math.min(imageWidth, maxAllowedWidth));
        imageView.setFitHeight(Math.min(imageHeight, maxAllowedHeight));
    }

    public Button getAddButton() {
        return addButton;
    }

    private void setActionsFromController() {
        fileOpenFolderItem.setOnAction(controller);
        fileSaveItem.setOnAction(controller);
        viewFitWindowItem.setOnAction(controller);
        //viewBoundingBoxColorItem.setOnAction(controller);
        nextButton.setOnAction(controller);
        previousButton.setOnAction(controller);

        addButton.setOnAction(controller);

        imageView.setOnMousePressed(controller::onMousePressed);
        imageView.setOnMouseDragged(controller::onMouseDragged);

//        selectionRectangle.setOnMouseEntered(controller::onSelectionRectangleMouseEntered);
//        selectionRectangle.setOnMousePressed(controller::onSelectionRectangleMousePressed);
//        selectionRectangle.setOnMouseDragged(controller::onSelectionRectangleMouseDragged);
    }

    private void setInternalBindingsAndListeners() {
        imagePane.widthProperty().addListener((value, oldValue, newValue) -> {
            double prefWidth = 0;
            if (imageView.getImage() != null)
                prefWidth = imageView.getImage().getWidth();
            imageView.setFitWidth(Math.min(prefWidth, newValue.doubleValue() - 2 * IMAGE_PADDING));
        });

        imagePane.heightProperty().addListener((value, oldValue, newValue) -> {
            double prefHeight = 0;
            if (imageView.getImage() != null)
                prefHeight = imageView.getImage().getHeight();
            imageView.setFitHeight(Math.min(prefHeight, newValue.doubleValue() - 2 * IMAGE_PADDING));
        });

        imageView.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
            selectionRectangle.setWidth(selectionRectangle.getWidth() * newValue.getWidth() / oldValue.getWidth());
            selectionRectangle.setHeight(selectionRectangle.getHeight() * newValue.getHeight() / oldValue.getHeight());

            selectionRectangle.setX(newValue.getMinX() + (selectionRectangle.getX() - oldValue.getMinX()) * newValue.getWidth() / oldValue.getWidth());
            selectionRectangle.setY(newValue.getMinY() + (selectionRectangle.getY() - oldValue.getMinY()) * newValue.getHeight() / oldValue.getHeight());
        });

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.brightnessProperty().bind(brightnessSlider.valueProperty());
        imageView.setEffect(colorAdjust);

        // not finished
        imagePane.setOnScroll(e -> {
            if (e.isControlDown()) {
                double delta = e.getDeltaY();

                double newFitWidth = Utils.clamp(imageView.getFitWidth() + delta,
                        0.25 * imagePane.getWidth(), imagePane.getWidth() - 2 * IMAGE_PADDING);
                double newFitHeight = Utils.clamp(imageView.getFitHeight() + delta,
                        0.25 * imagePane.getHeight(), imagePane.getHeight() - 2 * IMAGE_PADDING);

                imageView.setFitWidth(newFitWidth);
                imageView.setFitHeight(newFitHeight);
            }
        });

        imageView.imageProperty().addListener((value, oldValue, newValue) -> {
            selectionRectangle.setVisible(false);
            zoomSlider.setValue(1);
        });


        zoomSlider.valueProperty().addListener((value, oldValue, newValue) -> {
            double delta = (newValue.doubleValue() - oldValue.doubleValue()) * 500;

            double newFitWidth = Utils.clamp(imageView.getFitWidth() + delta,
                    0.25 * imagePane.getWidth(), imagePane.getWidth() - 2 * IMAGE_PADDING);
            double newFitHeight = Utils.clamp(imageView.getFitHeight() + delta,
                    0.25 * imagePane.getHeight(), imagePane.getHeight() - 2 * IMAGE_PADDING);

            imageView.setFitWidth(newFitWidth);
            imageView.setFitHeight(newFitHeight);
        });

        // Reset brightnessSlider on Label doubleclick
        brightnessLabel.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2){
                brightnessSlider.setValue(0);
            }
        });

        //selectionRectangle.strokeProperty().bind(boundingBoxColorPicker.valueProperty());
        selectionRectangle.confineTo(imageView.boundsInParentProperty());

        // To remove settingsToolbar when it is not visible.
        settingsToolBar.managedProperty().bind(settingsToolBar.visibleProperty());
        settingsToolBar.visibleProperty().bind(viewShowSettingsItem.selectedProperty());

        boundingBoxItemTableView.getSelectionModel().selectedItemProperty().addListener((value, oldValue, newValue) -> {
            if(newValue != null){
                selectionRectangle.setStroke(newValue.getColor());
            }
        });

    }

    public void setMousePressed(double x, double y) {
        mousePressed.setX(x);
        mousePressed.setY(y);
    }

    public double getMousePressedX() {
        return mousePressed.getX();
    }

    public TableView<BoundingBoxItem> getBoundingBoxItemTableView() {
        return boundingBoxItemTableView;
    }

    public double getMousePressedY() {
        return mousePressed.getY();
    }

    public ToolBar getNavBar(){
        return navBar;
    }

    public TextField getNameInput() {
        return nameInput;
    }

    public ColorPicker getBoundingBoxColorPicker() {
        return boundingBoxColorPicker;
    }

    public void displayErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private class ColorTableCell extends TableCell<BoundingBoxItem, Color> {

        @Override
        protected void updateItem(Color item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || getTableRow() == null) {
                setText(null);
                setGraphic(null);
                setStyle("-fx-background-color: transparent;");
            } else {
                BoundingBoxItem row = getTableRow().getItem();
                //setText(item.toString());
                if(row != null){
                    setStyle("-fx-background-color: " +
                            row.getColor().toString().replace("0x", "#") + ";");
                }

            }
        }
    }
}
