package io.kyberorg.yalsee.exception;

import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.err.RawServerErrorView;
import io.kyberorg.yalsee.ui.err.ServerErrorLoopView;

/**
 * Exception to be thrown by {@link ServerErrorLoopView} and {@link RawServerErrorView} handles it.
 * Most probably indicates exception at {@link MainView}.
 *
 * @since 3.8
 */
public class RepeatableServerException extends YalseeException {
}
