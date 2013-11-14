$registerSplashScreen({
    id: "process_dashboard_perspective.splash",
    templateUrl: "process_dashboard_perspective.splash.html",
    title: function () {
        return "Help";
    },
    display_next_time: true,
    interception_points: ["DashboardPerspective"]
});