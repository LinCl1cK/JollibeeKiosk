module kioskapp {
    // Requires the JavaFX modules necessary for the UI components.
    // javafx.controls implicitly requires javafx.graphics and javafx.base.
    requires javafx.controls;
    requires javafx.fxml; // If you were using FXML for UI definition, which you're not explicitly here but it's good practice to include for JavaFX apps.
    requires java.base; // Implicitly required, but can be added for clarity, especially for features like Locale.

    // Exports the main application package and view packages
    // so that JavaFX can reflectively access your controllers and application class.
    exports kioskapp; // Exports the main application package
    exports kioskapp.manager; // Exports manager classes
    exports kioskapp.model;   // Exports model classes
    exports kioskapp.view;    // Exports view (controller) classes
}
