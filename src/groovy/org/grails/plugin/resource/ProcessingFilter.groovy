package org.grails.plugin.resource

import javax.servlet.*
import org.springframework.web.context.support.WebApplicationContextUtils
import grails.util.Environment

class ProcessingFilter implements Filter {
    def resourceService
    
    boolean adhoc
    
    void init(FilterConfig config) throws ServletException {
        adhoc = config.getInitParameter('adhoc') == 'true'
        
        def applicationContext = WebApplicationContextUtils.getWebApplicationContext(config.servletContext)
        resourceService = applicationContext.resourceService
    }

    void destroy() {
    }

    void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException {

        def debugging = resourceService.isDebugMode(request)
        request['resources.debug'] = debugging
        if (!debugging) {
            if (adhoc) {
                resourceService.processAdHocResource(request, response)
            } else {
                resourceService.processDeclaredResource(request, response)
            }
            // we've done it, don't call any other filters
        } else {
            chain.doFilter(request, response)
        }
    }
}