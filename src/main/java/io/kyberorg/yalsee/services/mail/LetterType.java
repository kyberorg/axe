package io.kyberorg.yalsee.services.mail;

/**
 * Letter type and its template.
 * <p>
 * Value of {@link #getTemplateFile()} should correspond with
 * template file name located in /resources/templates/content folder.
 */
public enum LetterType {
    SERVER_ERROR("serverError.ftl");

    private final String templateFile;

    LetterType(final String template) {
        this.templateFile = template;
    }

    public String getTemplateFile() {
        return templateFile;
    }
}
