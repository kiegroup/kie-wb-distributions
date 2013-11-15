$registerSplashScreen({
    id: 'deployment.splash',
    templateUrl: 'deployment.splash.html',
    title: function () {
        return 'Deployments';
    },
    display_next_time: true,
    interception_points: ['Deployments']
});
