$registerSplashScreen({
    id: "authoring_perspective.splash",
    templateUrl: "authoring_perspective.splash.html",
    title: function () {
        return "Authoring quick start";
    },
    display_next_time: true,
    interception_points: ["org.kie.workbench.client.perspectives.DroolsAuthoringPerspective"]
});