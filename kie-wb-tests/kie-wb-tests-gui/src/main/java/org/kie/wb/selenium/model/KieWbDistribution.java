/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.wb.selenium.model;

import java.util.Arrays;
import java.util.Optional;

public enum KieWbDistribution {

    KIE_WB,

    KIE_DROOLS_WB,

    KIE_WB_MONITORING;

    public static Optional<KieWbDistribution> fromWarNameString(final String warName) {
        return Arrays.asList(KieWbDistribution.values()).stream().filter(distro -> distro.getWarName().equals(warName)).findFirst();
    }

    public String getWarName() {
        return this.name().toLowerCase().replace("_",
                                                 "-");
    }
}
