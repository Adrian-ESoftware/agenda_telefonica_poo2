module org.agenda_poo2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires jakarta.persistence;
    requires jdk.compiler;
    requires org.hibernate.orm.core;

    exports org.agenda_poo2;
    opens org.agenda_poo2 to javafx.fxml;
    opens org.model to org.hibernate.orm.core;
}