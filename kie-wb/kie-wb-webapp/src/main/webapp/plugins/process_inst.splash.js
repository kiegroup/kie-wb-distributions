$registerSplashScreen({
    id: 'process-inst.splash',
    templateUrl: 'process-inst.splash.html',
    title: function () {
        return 'Process Instances';
    },
    display_next_time: true,
    interception_points: ['org.jbpm.console.ng.pr.client.perspectives.ProcessInstancesPerspective']
});
