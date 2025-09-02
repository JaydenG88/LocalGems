package com.localgems.localgems_backend.service;
import org.springframework.stereotype.Service;
import com.localgems.localgems_backend.model.Business;
import com.localgems.localgems_backend.repository.BusinessRepository;

import java.lang.StackWalker.Option;
import java.util.*;

@Service
public class BusinessService {
    
    private final BusinessRepository businessRepository;

    public BusinessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }


}
