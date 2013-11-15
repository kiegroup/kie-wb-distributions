$registerSplashScreen({
    id: 'process-def.splash',
    templateUrl: 'process-def.splash.html',
    title: function () {
        return 'Process Definitions';
    },
    display_next_time: true,
    interception_points: ['org.jbpm.console.ng.pr.client.perspectives.ProcessDefinitionsPerspective']
});
