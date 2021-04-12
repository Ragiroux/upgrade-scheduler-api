package com.startree.upgradescheduler.controller;

import com.startree.upgradescheduler.domain.State;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/scheduler")
public class SchedulerController {

    @GetMapping("/state")
    public State getState(@RequestParam("clusterId") Long clusterId) {

        //find cluster in DB
        //get latest patch
        //compare if upgraded needed
        //use rollout strategy
        //return new state for managed cluster


        return new State(1L, LocalDateTime.now(), "", "1.0.0", LocalDateTime.now().plusDays(7));
    }
}
