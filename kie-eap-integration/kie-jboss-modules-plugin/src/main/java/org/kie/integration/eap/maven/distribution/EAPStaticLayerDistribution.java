/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.integration.eap.maven.distribution;

import org.kie.integration.eap.maven.model.graph.EAPModulesGraph;
import org.kie.integration.eap.maven.model.layer.EAPLayer;
import org.kie.integration.eap.maven.util.EAPArtifactsHolder;
import org.kie.integration.eap.maven.util.EAPConstants;

import java.util.Map;

public class EAPStaticLayerDistribution {
    private String distributionName;
    private EAPLayer staticLayer;
    private EAPLayer baseLayer;
    private EAPArtifactsHolder artifactsHolder;

    private EAPModulesGraph graph;
    private String printedDistro;
    private Boolean includedOptionalDependencies;

    public EAPStaticLayerDistribution(String distributionName, EAPModulesGraph graph) {
        this.distributionName = distributionName;
        this.graph = graph;
        this.includedOptionalDependencies = null;
    }

    public String print() {
        if (printedDistro != null) return printedDistro;

        StringBuilder result = new StringBuilder(EAPConstants.NEW_LINE);
        result.append("********************************************************************************************").append(EAPConstants.NEW_LINE);
        result.append("Distribution ").append(distributionName).append(EAPConstants.NEW_LINE);
        if (baseLayer != null) result.append("Base EAP: ").append(baseLayer.getName()).append(EAPConstants.NEW_LINE);
        if (baseLayer != null) result.append("Base EAP modules count: ").append(baseLayer.getModules().size()).append(EAPConstants.NEW_LINE);
        if (staticLayer != null) result.append("Static modues count: ").append(staticLayer.getModules().size()).append(EAPConstants.NEW_LINE);
        if (baseLayer != null && staticLayer != null) result.append("Total modues count: ").append(staticLayer.getModules().size() + baseLayer.getModules().size()).append(EAPConstants.NEW_LINE);
        if (includedOptionalDependencies != null && includedOptionalDependencies) result.append("Scanned optional dependencies included.").append(EAPConstants.NEW_LINE);
        else if (includedOptionalDependencies != null && !includedOptionalDependencies) result.append("Scanned optional dependencies not included.").append(EAPConstants.NEW_LINE);
        result.append("********************************************************************************************").append(EAPConstants.NEW_LINE);

        if (graph != null) result.append(graph.print());
        // if (artifactsHolder != null) result.append(printArtifactResolutionModulesMapping());

        return printedDistro = result.toString();
    }

    protected String printArtifactResolutionModulesMapping() {
        StringBuilder result = new StringBuilder();
        Map<String, String> mappings = artifactsHolder.getMappedCoordinates();
        if (mappings != null && !mappings.isEmpty()) {
            result.append("****************************************************************************************").append(EAPConstants.NEW_LINE);
            result.append("+++++++++++ Artifact resolution perfomed for each module ++++++++++++++++++++").append(EAPConstants.NEW_LINE);
            for (Map.Entry<String, String> entry : mappings.entrySet()) {
                String allCords = entry.getKey();
                String shortCords = entry.getValue();
                StringBuilder line = new StringBuilder();
                line.append("'").append(allCords).append("' <-> '").append(shortCords).append("'").append(EAPConstants.NEW_LINE);
                result.append(line.toString());
            }
            result.append("****************************************************************************************").append(EAPConstants.NEW_LINE);
        }

        return result.toString();
    }

    public void setStaticLayer(EAPLayer staticLayer) {
        this.staticLayer = staticLayer;
    }

    public void setBaseLayer(EAPLayer baseLayer) {
        this.baseLayer = baseLayer;
    }

    public void setArtifactsHolder(EAPArtifactsHolder artifactsHolder) {
        this.artifactsHolder = artifactsHolder;
    }

    public String getDistributionName() {
        return distributionName;
    }

    public EAPLayer getStaticLayer() {
        return staticLayer;
    }

    public EAPLayer getBaseLayer() {
        return baseLayer;
    }

    public EAPModulesGraph getGraph() {
        return graph;
    }

    public Boolean getIncludedOptionalDependencies() {
        return includedOptionalDependencies;
    }

    public void setIncludedOptionalDependencies(Boolean includedOptionalDependencies) {
        this.includedOptionalDependencies = includedOptionalDependencies;
    }
}
