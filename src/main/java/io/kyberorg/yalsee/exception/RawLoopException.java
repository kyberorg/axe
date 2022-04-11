package io.kyberorg.yalsee.exception;

import io.kyberorg.yalsee.ui.pages.err.raw500.RawServerErrorPage;

/**
 * Exception, which not intended to be thrown, just {@link RawServerErrorPage} handles it.
 *
 * @since 3.11
 */
public class RawLoopException extends YalseeException {
}
