package com.bentoco.catalog.configurations.middlewares;

import com.bentoco.catalog.core.model.Profile;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Getter
@Setter
@Component
@RequestScope
public class RequestContext {
    private Profile profile;
}
