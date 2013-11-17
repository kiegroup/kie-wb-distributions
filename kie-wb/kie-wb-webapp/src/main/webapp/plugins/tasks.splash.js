$registerSplashScreen({
    id: 'tasks.splash',
    templateUrl: 'tasks.splash.html',
    title: function () {
        return 'Task Management';
    },
    display_next_time: true,
    interception_points: ['Tasks']
});
