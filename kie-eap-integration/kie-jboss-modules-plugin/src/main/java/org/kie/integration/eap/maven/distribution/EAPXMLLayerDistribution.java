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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.annotations.Component;
import org.kie.integration.eap.maven.model.graph.EAPModuleGraphNode;
import org.kie.integration.eap.maven.model.graph.EAPModuleGraphNodeDependency;
import org.kie.integration.eap.maven.model.graph.EAPModuleGraphNodeResource;
import org.kie.integration.eap.maven.model.graph.EAPModulesGraph;
import org.kie.integration.eap.maven.model.graph.distribution.EAPModuleGraphDistributionNode;
import org.kie.integration.eap.maven.model.graph.distribution.EAPModuleGraphDistributionNodeDependency;
import org.kie.integration.eap.maven.model.graph.distribution.EAPModuleGraphDistributionNodeResource;
import org.kie.integration.eap.maven.model.graph.distribution.EAPModulesDistributionGraph;
import org.kie.integration.eap.maven.util.EAPArtifactUtils;
import org.kie.integration.eap.maven.util.EAPXMLUtils;
import org.sonatype.aether.artifact.Artifact;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// TODO: Add persistence new distribution properties.
@Component( role = EAPLayerDistributionManager.class )
public class EAPXMLLayerDistribution implements EAPLayerDistributionManager {

    private static final String ELEMENT_STATIC_LAYER = "staticLayer";
    private static final String ATTR_STATIC_LAYER_NAME = "name";
    private static final String ELEMENT_MODULES = "modules";
    private static final String ELEMENT_MODULE = "module";
    private static final String ATTR_MODULE_NAME = "name";
    private static final String ATTR_MODULE_SLOT = "slot";
    private static final String ELEMENT_RESOURCES= "resources";
    private static final String ELEMENT_RESOURCE = "resource";
    private static final String ATTR_RESOURCE_IS_ADDED = "isAdded";
    private static final String ATTR_RESOURCE_NAME = "name";
    private static final String ELEMENT_GROUP_ID  = "groupId";
    private static final String ELEMENT_ARTIFACT_ID = "artifactId";
    private static final String ELEMENT_VERSION = "version";
    private static final String ELEMENT_TYPE = "type";
    private static final String ELEMENT_FILE_NAME = "fileName";
    private static final String ELEMENT_DEPENDENCIES = "dependencies";
    private static final String ELEMENT_DEPENDENCY = "dependency";
    private static final String ATTR_DEP_IS_EXPORT = "isExport";
    private static final String ATTR_DEP_IS_OPTIONAL = "isOptional";
    private static final String ATTR_DEP_IS_MISSING = "isMissing";
    private static final String ATTR_DEP_SERVICES = "services";
    private static final String ATTR_DEP_META_INF = "metaInf";
    private static final String ATTR_DEP_NAME = "name";
    private static final String ATTR_DEP_SLOT = "slot";

    @Override
    public EAPStaticLayerDistribution read(Object input) throws Exception {
        EAPModulesDistributionGraph result = null;
        String xmlContent = null;

        try {
            xmlContent = (String) input;
        } catch (Exception e) {
            throw  new UnsupportedOperationException("Only String input allowed for this manager.");
        }

        if (xmlContent != null) {

            // convert String into InputStream
            InputStream is = new ByteArrayInputStream(xmlContent.getBytes());

            EAPXMLUtils eapxmlUtils = new EAPXMLUtils(is);
            Document doc = eapxmlUtils.getDocument();

            String distroName = EAPXMLUtils.getAttributeValue(doc, ATTR_STATIC_LAYER_NAME);
            result = new EAPModulesDistributionGraph(distroName);

            NodeList modulesNodes = doc.getElementsByTagName(ELEMENT_MODULE);
            if (modulesNodes != null) {
                for (int temp = 0; temp < modulesNodes.getLength(); temp++) {
                    Node nNode = modulesNodes.item(temp);
                    EAPModuleGraphNode moduleNode = parseModule(nNode);
                    result.addNode(moduleNode);
                }
            }
        }


        return new EAPStaticLayerDistribution(result.getDistributionName(), result);
    }

    protected EAPModuleGraphNode parseModule(Node module) {
        Element element = (Element) module;
        String moduleName = EAPXMLUtils.getAttributeValue(module, ATTR_MODULE_NAME);
        String moduleSlot = EAPXMLUtils.getAttributeValue(module, ATTR_MODULE_SLOT);
        String moduleLocation = null;

        // Create the node instance for this module.
        EAPModuleGraphDistributionNode result = new EAPModuleGraphDistributionNode(moduleName, moduleLocation, moduleSlot);

        // Parse resources.
        NodeList resourcesNodes = element.getElementsByTagName(ELEMENT_RESOURCE);
        if (resourcesNodes != null) {
            for (int temp = 0; temp < resourcesNodes.getLength(); temp++) {
                Node nNode = resourcesNodes.item(temp);
                EAPModuleGraphNodeResource resource = parseResource(nNode);
                result.addResource(resource);
            }
        }

        // Parse dependencies.
        NodeList dependenciesNodes = element.getElementsByTagName(ELEMENT_DEPENDENCY);
        if (dependenciesNodes != null) {
            for (int temp = 0; temp < dependenciesNodes.getLength(); temp++) {
                Node nNode = dependenciesNodes.item(temp);
                EAPModuleGraphNodeDependency dependency = parseDependency(nNode);
                result.addDependency(dependency);
            }
        }

        return result;
    }

    protected EAPModuleGraphNodeResource parseResource(Node node) {
        Element element = (Element) node;
        String name = EAPXMLUtils.getAttributeValue(node, ATTR_RESOURCE_NAME);
        String isAddedRaw = EAPXMLUtils.getAttributeValue(node, ATTR_RESOURCE_IS_ADDED);
        String fileName = element.getElementsByTagName(ELEMENT_FILE_NAME).item(0).getFirstChild().getNodeValue();

        // Parse artifact.
        NodeList artifactIdNodeList = element.getElementsByTagName(ELEMENT_ARTIFACT_ID);
        NodeList groupIdNodeList = element.getElementsByTagName(ELEMENT_GROUP_ID);
        NodeList versionNodeList = element.getElementsByTagName(ELEMENT_VERSION);
        NodeList typeNodeList = element.getElementsByTagName(ELEMENT_TYPE);

        String artifactId = artifactIdNodeList.item(0).getFirstChild().getNodeValue();
        String groupId = groupIdNodeList.item(0).getFirstChild().getNodeValue();
        String version = versionNodeList.item(0).getFirstChild().getNodeValue();
        String type = typeNodeList.item(0).getFirstChild().getNodeValue();
        Artifact artifact = EAPArtifactUtils.createArtifact(groupId, artifactId, version, type);

        EAPModuleGraphDistributionNodeResource result = new EAPModuleGraphDistributionNodeResource(name, fileName, Boolean.valueOf(isAddedRaw));
        result.setArtifact(artifact);

        return result;
    }

    protected EAPModuleGraphNodeDependency parseDependency(Node node) {
        Element element = (Element) node;
        String name = EAPXMLUtils.getAttributeValue(node, ATTR_DEP_NAME);
        String slot = EAPXMLUtils.getAttributeValue(node, ATTR_DEP_SLOT);
        String isOptionalRaw = EAPXMLUtils.getAttributeValue(node, ATTR_DEP_IS_OPTIONAL);
        String isMissingRaw = EAPXMLUtils.getAttributeValue(node, ATTR_DEP_IS_MISSING);
        String isExportlRaw = EAPXMLUtils.getAttributeValue(node, ATTR_DEP_IS_EXPORT);
        String services = EAPXMLUtils.getAttributeValue(node, ATTR_DEP_SERVICES);
        String metaInf = EAPXMLUtils.getAttributeValue(node, ATTR_DEP_META_INF);

        return new EAPModuleGraphDistributionNodeDependency(name, slot, Boolean.valueOf(isOptionalRaw), Boolean.valueOf(isMissingRaw),
                Boolean.valueOf(isExportlRaw),services, metaInf);
    }

    @Override
    public Object write(EAPStaticLayerDistribution distro) throws Exception {
        EAPXMLUtils eapxmlUtils = EAPXMLUtils.newInstance();
        // Generate the DOM model.
        writeDistributionProperties(eapxmlUtils, distro.getGraph());
        // Write the file.
        return writeDistro(eapxmlUtils);
    }

    protected String writeDistro(EAPXMLUtils eapxmlUtils) throws TransformerException, IOException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(eapxmlUtils.getDocument());
        StringWriter outWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(outWriter);
        transformer.transform(source, streamResult);
        return outWriter.getBuffer().toString();
    }

    protected void writeDistributionProperties(EAPXMLUtils eapxmlUtils, EAPModulesGraph graph) throws MojoExecutionException {
        if (graph != null) {
            Document doc = eapxmlUtils.getDocument();

            // Generate the XML root staticLayer element
            Element staticLayerElement = doc.createElement(ELEMENT_STATIC_LAYER);
            staticLayerElement.setAttribute(ATTR_STATIC_LAYER_NAME, graph.getDistributionName());
            doc.appendChild(staticLayerElement);

            // Generate each module definition.
            Collection<EAPModuleGraphNode> nodes = graph.getNodes();
            if (nodes != null) {
                Element modulesElement = eapxmlUtils.createElement(ELEMENT_MODULES, null, staticLayerElement);

                for (EAPModuleGraphNode node : nodes) {

                    // Create the module element and its attributes.
                    Map<String, String> statiModuleProperties = new HashMap<String, String>();
                    statiModuleProperties.put(ATTR_MODULE_NAME, node.getName());
                    statiModuleProperties.put(ATTR_MODULE_SLOT, node.getSlot());
                    Element moduleElement = eapxmlUtils.createElement(ELEMENT_MODULE, statiModuleProperties, modulesElement);

                    // Cretae the resources elements.
                    Collection<EAPModuleGraphNodeResource> resources = node.getResources();
                    if (resources != null && !resources.isEmpty()) {
                        Element resourcesElement = eapxmlUtils.createElement(ELEMENT_RESOURCES, null, moduleElement);
                        for (EAPModuleGraphNodeResource resource : resources) {
                            createResource(eapxmlUtils, resourcesElement, resource);
                        }
                    }

                    // Cretae the dependencies elements.
                    Collection<EAPModuleGraphNodeDependency> dependencies = node.getDependencies();
                    if (dependencies != null && !dependencies.isEmpty()) {
                        Element dependenciesElement = eapxmlUtils.createElement(ELEMENT_DEPENDENCIES, null, moduleElement);
                        for (EAPModuleGraphNodeDependency dependency : dependencies) {
                            createDependency(eapxmlUtils, dependenciesElement, dependency);
                        }
                    }

                }
            }

        }
    }


    protected Element createResource(EAPXMLUtils eapxmlUtils, Element parent, EAPModuleGraphNodeResource resource) {
        Element result = null;

        if (resource != null) {
            Document doc = eapxmlUtils.getDocument();

            Map<String, String> resourceProperties = new HashMap<String, String>();
            resourceProperties.put(ATTR_RESOURCE_IS_ADDED, Boolean.toString(resource.isAddAsResource()));
            resourceProperties.put(ATTR_RESOURCE_NAME, resource.getName());
            result = eapxmlUtils.createElement(ELEMENT_RESOURCE, resourceProperties, parent);

            Artifact resourceArtifact = null;
            try {
                resourceArtifact = (Artifact) resource.getResource();
            } catch (Exception e) {
                throw new UnsupportedOperationException("Only supported artifact resources.");
            }

            if (resourceArtifact != null) {
                String groupId = resourceArtifact.getGroupId();
                String artifactId = resourceArtifact.getArtifactId();
                String version = EAPArtifactUtils.toSnaphostVersion(resourceArtifact);
                String type = resourceArtifact.getExtension();
                String fileName = resource.getFileName();

                Element groupIdElement = eapxmlUtils.createElement(ELEMENT_GROUP_ID, null, result);
                groupIdElement.appendChild(doc.createTextNode(groupId));

                Element artifactIdElement = eapxmlUtils.createElement(ELEMENT_ARTIFACT_ID, null, result);
                artifactIdElement.appendChild(doc.createTextNode(artifactId));

                Element versionElement = eapxmlUtils.createElement(ELEMENT_VERSION, null, result);
                versionElement.appendChild(doc.createTextNode(version));

                Element typeElement = eapxmlUtils.createElement(ELEMENT_TYPE, null, result);
                typeElement.appendChild(doc.createTextNode(type));

                Element fileNameElement = eapxmlUtils.createElement(ELEMENT_FILE_NAME, null, result);
                fileNameElement.appendChild(doc.createTextNode(fileName));
            }

        }

        return result;
    }


    protected Element createDependency(EAPXMLUtils eapxmlUtils, Element parent, EAPModuleGraphNodeDependency dependency) {
        Element result = null;

        if (dependency != null) {
            Map<String, String> dependencyProperties = new HashMap<String, String>();
            dependencyProperties.put(ATTR_DEP_NAME, dependency.getName());
            dependencyProperties.put(ATTR_DEP_SLOT, dependency.getSlot());
            dependencyProperties.put(ATTR_DEP_IS_OPTIONAL, Boolean.toString(dependency.isOptional()));
            dependencyProperties.put(ATTR_DEP_IS_MISSING, Boolean.toString(dependency.isMissing()));
            dependencyProperties.put(ATTR_DEP_IS_EXPORT, Boolean.toString(dependency.isExport()));
            dependencyProperties.put(ATTR_DEP_SERVICES, dependency.getServices());
            dependencyProperties.put(ATTR_DEP_META_INF, dependency.getMetaInf());
            result = eapxmlUtils.createElement(ELEMENT_DEPENDENCY, dependencyProperties, parent);
        }

        return result;
    }
}
