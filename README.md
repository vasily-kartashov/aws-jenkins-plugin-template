AWS Jenkins plugin template
===========================

AWS provides you with an almost unlimited possibilities to configure you environments. The deployments to this highly 
customized topologies can become very elaborate. This template provides you with a simple skeleton of a Jenkins plugin,
that allows you to write your custom deployment logic by using AWS Java SDK.

Instructions
---------------------------
- Update your maven settings file as described here: 
  https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial#Plugintutorial-SettingUpEnvironment
- Execute ```mvn clean hpi:run```

Implementing deployment algorithm
---------------------------
Put your deployment logic into PluginRecorder.perform method. Don't forget to return the right return code. 
The code is not that numerous so you should be able to extend it as you need.