package com.playhub.common.errors.managment.web;

import java.util.Set;

public interface TypeAwareProblemDetailResolver extends ProblemDetailResolver {

    Set<Class<? extends Throwable>> resolvableExceptions();

}
