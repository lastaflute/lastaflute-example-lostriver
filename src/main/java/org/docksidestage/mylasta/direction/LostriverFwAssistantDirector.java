/*
 * Copyright 2015-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.docksidestage.mylasta.direction;

import javax.annotation.Resource;

import org.docksidestage.mylasta.direction.sponsor.LostriverActionAdjustmentProvider;
import org.docksidestage.mylasta.direction.sponsor.LostriverApiFailureHook;
import org.docksidestage.mylasta.direction.sponsor.LostriverCookieResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.LostriverCurtainBeforeHook;
import org.docksidestage.mylasta.direction.sponsor.LostriverJsonResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.LostriverListedClassificationProvider;
import org.docksidestage.mylasta.direction.sponsor.LostriverMailDeliveryDepartmentCreator;
import org.docksidestage.mylasta.direction.sponsor.LostriverMultipartRequestHandler;
import org.docksidestage.mylasta.direction.sponsor.LostriverSecurityResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.LostriverTimeResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.LostriverUserLocaleProcessProvider;
import org.docksidestage.mylasta.direction.sponsor.LostriverUserTimeZoneProcessProvider;
import org.lastaflute.core.direction.CachedFwAssistantDirector;
import org.lastaflute.core.direction.CurtainBeforeHook;
import org.lastaflute.core.direction.FwAssistDirection;
import org.lastaflute.core.direction.FwCoreDirection;
import org.lastaflute.core.json.JsonResourceProvider;
import org.lastaflute.core.security.InvertibleCryptographer;
import org.lastaflute.core.security.OneWayCryptographer;
import org.lastaflute.core.security.SecurityResourceProvider;
import org.lastaflute.core.time.TimeResourceProvider;
import org.lastaflute.db.dbflute.classification.ListedClassificationProvider;
import org.lastaflute.db.direction.FwDbDirection;
import org.lastaflute.web.api.ApiFailureHook;
import org.lastaflute.web.direction.FwWebDirection;
import org.lastaflute.web.path.ActionAdjustmentProvider;
import org.lastaflute.web.ruts.multipart.MultipartResourceProvider;
import org.lastaflute.web.servlet.cookie.CookieResourceProvider;
import org.lastaflute.web.servlet.filter.cors.CorsHook;
import org.lastaflute.web.servlet.request.UserLocaleProcessProvider;
import org.lastaflute.web.servlet.request.UserTimeZoneProcessProvider;

/**
 * @author jflute
 */
public class LostriverFwAssistantDirector extends CachedFwAssistantDirector {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private LostriverConfig config;

    // ===================================================================================
    //                                                                              Assist
    //                                                                              ======
    @Override
    protected void prepareAssistDirection(FwAssistDirection direction) {
        direction.directConfig(nameList -> nameList.add("lostriver_config.properties"), "lostriver_env.properties");
    }

    // ===================================================================================
    //                                                                               Core
    //                                                                              ======
    @Override
    protected void prepareCoreDirection(FwCoreDirection direction) {
        // this configuration is on lostriver_env.properties because this is true only when development
        direction.directDevelopmentHere(config.isDevelopmentHere());

        // titles of the application for logging are from configurations
        direction.directLoggingTitle(config.getDomainTitle(), config.getEnvironmentTitle());

        // this configuration is on sea_env.properties because it has no influence to production
        // even if you set true manually and forget to set false back
        direction.directFrameworkDebug(config.isFrameworkDebug()); // basically false

        // you can add your own process when your application is booting
        direction.directCurtainBefore(createCurtainBeforeHook());

        direction.directSecurity(createSecurityResourceProvider());
        direction.directTime(createTimeResourceProvider());
        direction.directJson(createJsonResourceProvider());
        direction.directMail(createMailDeliveryDepartmentCreator().create());
    }

    protected CurtainBeforeHook createCurtainBeforeHook() {
        return new LostriverCurtainBeforeHook();
    }

    protected SecurityResourceProvider createSecurityResourceProvider() { // #change_it_first
        final String appMessage = "if needed, fix settings at " + getClass().getSimpleName();
        final InvertibleCryptographer inver = InvertibleCryptographer.createUnsupportedCipher(appMessage);
        final OneWayCryptographer oneWay = OneWayCryptographer.createSha256Cryptographer();
        return new LostriverSecurityResourceProvider(inver, oneWay);
    }

    protected TimeResourceProvider createTimeResourceProvider() {
        return new LostriverTimeResourceProvider(config);
    }

    protected JsonResourceProvider createJsonResourceProvider() {
        return new LostriverJsonResourceProvider();
    }

    protected LostriverMailDeliveryDepartmentCreator createMailDeliveryDepartmentCreator() {
        return new LostriverMailDeliveryDepartmentCreator(config);
    }

    // ===================================================================================
    //                                                                                 DB
    //                                                                                ====
    @Override
    protected void prepareDbDirection(FwDbDirection direction) {
        direction.directClassification(createListedClassificationProvider());
    }

    protected ListedClassificationProvider createListedClassificationProvider() {
        return new LostriverListedClassificationProvider();
    }

    // ===================================================================================
    //                                                                                Web
    //                                                                               =====
    @Override
    protected void prepareWebDirection(FwWebDirection direction) {
        direction.directRequest(createUserLocaleProcessProvider(), createUserTimeZoneProcessProvider());
        direction.directCookie(createCookieResourceProvider());
        direction.directAdjustment(createActionAdjustmentProvider());
        direction.directMessage(nameList -> nameList.add("lostriver_message"), "lostriver_label");
        direction.directApiCall(createApiFailureHook());
        direction.directCors(new CorsHook("http://localhost:5000")); // #change_it
        direction.directMultipart(createMultipartResourceProvider());
    }

    protected UserLocaleProcessProvider createUserLocaleProcessProvider() {
        return new LostriverUserLocaleProcessProvider();
    }

    protected UserTimeZoneProcessProvider createUserTimeZoneProcessProvider() {
        return new LostriverUserTimeZoneProcessProvider();
    }

    protected CookieResourceProvider createCookieResourceProvider() { // #change_it_first
        final String appMessage = "if needed, fix settings at " + getClass().getSimpleName();
        final InvertibleCryptographer cr = InvertibleCryptographer.createUnsupportedCipher(appMessage);
        return new LostriverCookieResourceProvider(config, cr);
    }

    protected ActionAdjustmentProvider createActionAdjustmentProvider() {
        return new LostriverActionAdjustmentProvider();
    }

    protected ApiFailureHook createApiFailureHook() {
        return new LostriverApiFailureHook();
    }

    protected MultipartResourceProvider createMultipartResourceProvider() {
        return () -> new LostriverMultipartRequestHandler();
    }
}
