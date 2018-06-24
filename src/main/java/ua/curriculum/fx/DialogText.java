package ua.curriculum.fx;

/**
 * Created by AnGo on 05.06.2017.
 */
public class DialogText {
    private String titleText;
    private String headerText;
    private String contentText;

    public DialogText() {
    }

    public DialogText(String titleText, String headerText, String contentText) {
        this.titleText = titleText;
        this.headerText = headerText;
        this.contentText = contentText;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DialogText{");
        sb.append("titleText='").append(titleText).append('\'');
        sb.append(", headerText='").append(headerText).append('\'');
        sb.append(", contentText='").append(contentText).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DialogText that = (DialogText) o;

        if (titleText != null ? !titleText.equals(that.titleText) : that.titleText != null) return false;
        if (headerText != null ? !headerText.equals(that.headerText) : that.headerText != null) return false;
        return contentText != null ? contentText.equals(that.contentText) : that.contentText == null;
    }

    @Override
    public int hashCode() {
        int result = titleText != null ? titleText.hashCode() : 0;
        result = 31 * result + (headerText != null ? headerText.hashCode() : 0);
        result = 31 * result + (contentText != null ? contentText.hashCode() : 0);
        return result;
    }
}
