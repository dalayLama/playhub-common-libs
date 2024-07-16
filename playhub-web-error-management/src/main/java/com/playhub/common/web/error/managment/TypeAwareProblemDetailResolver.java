package com.playhub.common.web.error.managment;

import java.util.Set;

public interface TypeAwareProblemDetailResolver extends ProblemDetailResolver {

    Set<Class<? extends Throwable>> resolvableExceptions();

}
