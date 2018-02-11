package ca.n4dev.aegaeon.server.controller;

import ca.n4dev.aegaeon.server.service.BaseServiceTest;
import org.junit.Before;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

/**
 * BaseIntegratedControllerTest.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Feb 08 - 2018
 */
public abstract class BaseIntegratedControllerTest<C> extends BaseServiceTest implements ApplicationContextAware {

    protected MockMvc mockMvc;
    protected C controller;
    protected ApplicationContext applicationContext;

    @Autowired
    ControllerErrorInterceptor controllerErrorInterceptor;

    @Autowired
    private WebApplicationContext context;


    /**
     * Gets the class of the controller that is under tests.
     *
     * @return the requested class
     */
    protected abstract Class<C> getControllerClass();

    /**
     * Gets an instance of the controller.
     *
     * @return the controller
     */
    protected final C getControllerInstance() {
        if (controller == null) {
            controller = applicationContext.getAutowireCapableBeanFactory().getBean(getControllerClass());
        }
        return controller;
    }

    @Before
    public void init() {

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                 //.standaloneSetup(getControllerInstance())
                                 //.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                                 //                            new AuthenticationPrincipalArgumentResolver())
                                 //.setControllerAdvice(controllerErrorInterceptor)
                                 .apply(springSecurity())
                                 .build();
    }

    @Override
    public void setApplicationContext(ApplicationContext pApplicationContext) throws BeansException {
        applicationContext = pApplicationContext;
    }
}
