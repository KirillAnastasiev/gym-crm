package com.epam.laboratory.cucumber.hooks;

import com.epam.laboratory.cucumber.util.ContextHolder;
import io.cucumber.java.After;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ContextCleaningHook {

    private final ContextHolder contextHolder;

    @After
    public void clearContext() {
        contextHolder.clear();
    }
}
