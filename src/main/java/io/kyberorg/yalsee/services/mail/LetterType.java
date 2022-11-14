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

    /**
     * Provides filename of template linked to given {@link LetterType}.
     *
     * @return string with filename and extension (myTemplate.ftl).
     */
    public String getTemplateFile() {
        return templateFile;
    }
}
