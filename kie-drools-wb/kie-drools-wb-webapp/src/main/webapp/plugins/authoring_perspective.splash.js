$registerSplashScreen({
    id: "authoring_perspective.splash",
    templateUrl: "authoring_perspective.splash.html",
    title: function () {
        return "Help";
    },
    display_next_time: true,
    interception_points: ["org.kie.workbench.drools.client.perspectives.DroolsAuthoringPerspective"]
});