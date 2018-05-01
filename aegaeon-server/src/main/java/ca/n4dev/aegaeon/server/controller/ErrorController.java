package ca.n4dev.aegaeon.server.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import ca.n4dev.aegaeon.api.exception.Severity;
import ca.n4dev.aegaeon.server.config.ServerInfo;
import ca.n4dev.aegaeon.server.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * ErrorController.java
 *
 * A controller used to display an error page.
 *
 * @author rguillemette
 * @since 2.0.0 - Apr 30 - 2018
 */
@Controller
@RequestMapping(value = ErrorController.URL)
public class ErrorController extends BaseUiController {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final String URL = "/errors";
    public static final String VIEW = "error";

    private ServerInfo serverInfo;

    /**
     * Build this constructor with a label message source.
     *
     * @param pMessages The message source.
     */
    @Autowired
    public ErrorController(MessageSource pMessages, ServerInfo pServerInfo) {
        super(pMessages);
        serverInfo = pServerInfo;
    }

    @RequestMapping("")
    public ModelAndView error(@RequestParam(value = "type", required = false) String pErrorType,
                              @RequestParam(value = "message", required = false) String pErrorMessage,
                              @RequestParam(value = "severity", required = false) String pErrorSeverity,
                              Locale pLocale) {
        ModelAndView mv = new ModelAndView(VIEW);

        Severity severity = Severity.from(pErrorSeverity);

        String errorMessage = Utils.isNotEmpty(pErrorMessage) ?
                pErrorMessage : getLabel("page.error.list." + pErrorType, pLocale);

        mv.addObject("date", formatter.format(LocalDateTime.now()));
        mv.addObject("severity", severity.toString());
        mv.addObject("errorMessage", errorMessage);
        mv.addObject("errorType", pErrorType);
        mv.addObject("serverInfo", this.serverInfo);

        return mv;
    }
}
