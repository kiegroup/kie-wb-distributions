$registerSplashScreen({
    id: 'jobs.splash',
    templateUrl: 'jobs.splash.html',
    title: function () {
        return 'Async Jobs';
    },
    display_next_time: true,
    interception_points: ['Jobs']
});
