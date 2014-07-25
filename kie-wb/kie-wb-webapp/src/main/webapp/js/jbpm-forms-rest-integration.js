function jBPMFormsAPI() {

    var configs = new Object();
    var lastConfig;

    var listener = function dolisten(event){

        if (lastConfig) {
            if (!lastConfig.host.startsWith(event.origin)) return;

            try {
                var response = JSON.parse(event.data)
                if (response.status == 'success' && lastConfig.onsuccess) lastConfig.onsuccess(response.message);
                else if (lastConfig.onerror) lastConfig.onerror(response.message);
            } catch (e) {
                if (lastConfig.onerror) lastConfig.onerror(event.data);
            }
        }
    }

    var getXMLDoc = function(xml) {
        if (!xml) return;

        var xmlDoc;
        if (window.DOMParser) {
            var parser = new DOMParser();
            xmlDoc = parser.parseFromString(xml, "text/xml");
        } else { // Internet Explorer
            xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
            xmlDoc.async = false;
            xmlDoc.loadXML(xml);
        }
        return xmlDoc;
    }

    if (window.addEventListener){
        addEventListener("message", listener, false)
    } else {
        attachEvent("onmessage", listener)
    }

    var getRequest = function(config) {
        sendRequest(config, "GET");
    }

    var postRequest = function(config) {
        sendRequest(config, "POST");
    }

    var sendRequest = function(config, method) {
        if (!config) return;

        var ajaxHandler = new Object();

        if (window.XMLHttpRequest) {
            ajaxHandler.ajaxReq = new XMLHttpRequest();
        }
        else if (window.ActiveXObject) {
            ajaxHandler.ajaxReq = new ActiveXObject('Microsoft.XMLHTTP');
        }

        ajaxHandler.ajaxResponse = function() {
            // Only if req shows "complete"
            var readyState, status;
            readyState = ajaxHandler.ajaxReq.readyState;
            if (readyState == 4) {
                status = ajaxHandler.ajaxReq.status;
            }
            if (readyState == 4) {
                if (status == 200) {
                    if (config.onsuccess) config.onsuccess(ajaxHandler.ajaxReq.responseText);
                } else {
                    if (config.onerror) config.onerror(ajaxHandler.ajaxReq.responseText);
                }
            }

        }
        ajaxHandler.ajaxReq.onreadystatechange = ajaxHandler.ajaxResponse;
        ajaxHandler.ajaxReq.open(method, config.url, true);
        ajaxHandler.ajaxReq.send();
    }

    this.showStartProcessForm = function(hostUrl, deploymentId, processId, responseDiv, successCallback, errorCallback) {

        if (!hostUrl || !deploymentId || !processId || !responseDiv) return;

        if (hostUrl.charAt(hostUrl.length -1) != "/") hostUrl += "/";

        var config = {
            containerId: responseDiv,
            host: hostUrl,
            url: hostUrl + "rest/runtime/" + deploymentId + "/process/" + processId + "/startform",
            isProcess: true,
            deploymentId: deploymentId,
            processId: processId,
            formURL: null,
            onsuccess: function (responseText) {
                try {
                    var xmlDoc = getXMLDoc(responseText);

                    if (!xmlDoc) {
                        if (errorCallback) errorCallback(responseText);
                        return;
                    }

                    var status = xmlDoc.getElementsByTagName("status");

                    if (status && status.length > 0 && status[0].childNodes.length > 0) {
                        status = status[0].childNodes[0].nodeValue;

                        if (status == 'SUCCESS') {
                            var formURL = xmlDoc.getElementsByTagName("formUrl");
                            if (formURL && formURL.length > 0 && formURL[0].childNodes.length > 0) {
                                this.formURL = formURL[0].childNodes[0].nodeValue;
                                var html = "<iframe id='" + this.containerId + "_form' src='" + this.formURL + "' frameborder='0' style='width:100%; height:100%'></iframe>";
                                var targetDiv = document.getElementById(this.containerId);
                                targetDiv.innerHTML = html;
                                if (successCallback) successCallback(responseText);
                                return;
                            }
                        }

                    }
                    if (errorCallback) errorCallback(responseText);
                } catch (err) {
                    if (errorCallback) errorCallback("Unexpected error: " + err.message);
                    else alert("Unexpected error: " + err.message);
                }

            },
            onerror: errorCallback
        };
        configs[responseDiv] = config;

        postRequest(config);
    };


    this.startProcess = function(responseDiv, onsuccess, onerror) {
        var config = configs[responseDiv];
        if (config && config.isProcess) postAction(config, 'startProcess', onsuccess, onerror);
    };

    this.claimTask = function(responseDiv, onsuccess, onerror) {
        var config = configs[responseDiv];
        if (config && !config.isProcess) postAction(config, 'claimTask', onsuccess, onerror);
    };

    this.startTask = function(responseDiv, onsuccess, onerror) {
        var config = configs[responseDiv];
        if (config && !config.isProcess) postAction(config, 'startTask', onsuccess, onerror);
    };

    this.releaseTask = function(responseDiv, onsuccess, onerror) {
        var config = configs[responseDiv];
        if (config && !config.isProcess) postAction(config, 'releaseTask', onsuccess, onerror);
    };

    this.saveTask = function(responseDiv, onsuccess, onerror) {
        var config = configs[responseDiv];
        if (config && !config.isProcess) postAction(config, 'saveTask', onsuccess, onerror);
    };

    this.completeTask = function(responseDiv, onsuccess, onerror) {
        var config = configs[responseDiv];
        if (config && !config.isProcess) postAction(config, 'completeTask', onsuccess, onerror);
    };

    var postAction = function(config, action, onsuccess, onerror) {
        if (config && action) {
            var frame = document.getElementById(config.containerId + '_form').contentWindow;

            var request = '{"action":"'+ action + '",';
            if (config.isProcess) request+= '"processId":"' + config.processId + '", "domainId":"' + config.deploymentId +'"}';
            else request+= '"taskId":"' + config.taskId + '"}';
            frame.postMessage(request, config.formURL);
            lastConfig = config;
            if (onsuccess) lastConfig.onsuccess = onsuccess;
            if (onerror) lastConfig.onerror = onerror;
        }
    }

    this.showTaskForm = function (hostUrl, taskId, responseDiv, successCallback, errorCallback) {
        if (!hostUrl || !taskId || !responseDiv) return;

        if (hostUrl.charAt(hostUrl.length -1) != "/") hostUrl += "/";

        var config = {
            containerId: responseDiv,
            host: hostUrl,
            url: hostUrl + "rest/task/" + taskId + "/showTaskForm",
            isProcess: false,
            taskId: taskId,
            formURL: null,
            onsuccess: function (responseText) {
                try {
                    var xmlDoc = getXMLDoc(responseText);

                    if (!xmlDoc) {
                        if (errorCallback) errorCallback(responseText);
                        return;
                    }

                    var status = xmlDoc.getElementsByTagName("status");

                    if (status && status.length > 0 && status[0].childNodes.length > 0) {
                        status = status[0].childNodes[0].nodeValue;

                        if (status == 'SUCCESS') {
                            var formURL = xmlDoc.getElementsByTagName("formUrl");
                            if (formURL && formURL.length > 0 && formURL[0].childNodes.length > 0) {
                                this.formURL = formURL[0].childNodes[0].nodeValue;
                                var html = "<iframe id='" + this.containerId + "_form' src='" + this.formURL + "' frameborder='0' style='width:100%; height:100%'></iframe>";
                                var targetDiv = document.getElementById(this.containerId);
                                targetDiv.innerHTML = html;
                                if (successCallback) successCallback(responseText);
                                return;
                            }
                        }

                    }
                    if (errorCallback) errorCallback(responseText);
                } catch (err) {
                    if (errorCallback) errorCallback("Unexpected error: " + err.message);
                    else alert("Unexpected error: " + err.message);
                }
            },
            onerror: errorCallback
        };
        configs[responseDiv] = config;

        getRequest(config);
    }

    this.clearContainer = function (containerId) {
        if (containerId) {
            delete configs[containerId];
            document.getElementById(containerId).innerHTML = "";
        }
    }
};