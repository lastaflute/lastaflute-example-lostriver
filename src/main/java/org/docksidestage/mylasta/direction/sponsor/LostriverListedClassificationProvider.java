/*
 * Copyright 2015-2022 the original author or authors.
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
package org.docksidestage.mylasta.direction.sponsor;

import java.util.function.Function;

import org.dbflute.jdbc.ClassificationMeta;
import org.dbflute.optional.OptionalThing;
import org.docksidestage.mylasta.appcls.AppCDef;
import org.lastaflute.db.dbflute.classification.TypicalListedClassificationProvider;
import org.lastaflute.db.dbflute.exception.ProvidedClassificationNotFoundException;

/**
 * @author jflute
 */
public class LostriverListedClassificationProvider extends TypicalListedClassificationProvider { // basically for HTML response

    @Override
    protected Function<String, ClassificationMeta> chooseClassificationFinder(String projectName)
            throws ProvidedClassificationNotFoundException {
        return clsName -> null; // fixedly null for no DB application
    }

    @Override
    protected Function<String, ClassificationMeta> getDefaultClassificationFinder() {
        return clsName -> {
            return onAppCls(clsName).orElse(null); // null means not found
        };
    }

    protected OptionalThing<ClassificationMeta> onAppCls(String clsName) {
        return findMeta(AppCDef.DefMeta.class, clsName);
    }
}
