package ua.curriculum.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Calendar {
    private ObjectProperty<LocalDate> date;
    private Schoolyear schoolyear;
    private DateState dateState;
    private StringProperty description;

    public LocalDate getDate() {
        return date.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public Schoolyear getSchoolyear() {
        return schoolyear;
    }

    public void setSchoolyear(Schoolyear schoolyear) {
        this.schoolyear = schoolyear;
    }

    public DateState getDateState() {
        return dateState;
    }

    public void setDateState(DateState dateState) {
        this.dateState = dateState;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Calendar{");
        sb.append("date=").append(date);
        sb.append(", schoolyear=").append(schoolyear);
        sb.append(", dateState=").append(dateState);
        sb.append(", description=").append(description);
        sb.append('}');
        return sb.toString();
    }
}
