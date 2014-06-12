AWS Jenkins plugin template
===========================

AWS provides you with almost unlimited options to configure you environments. The deployments to this highly 
customized topologies can become very elaborate. This template provides you with a simple skeleton of a Jenkins plugin,
that allows you to write your custom deployment logic by using AWS Java SDK.

Instructions
---------------------------
- Update your maven settings file as described here: 
  https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial#Plugintutorial-SettingUpEnvironment
- Execute ```mvn clean hpi:run```

Implementing deployment algorithm
---------------------------
Put your deployment logic into ```PluginRecorder.perform``` method. Don't forget to return the right status code from 
this method. This plugin is not that big so you should be able to read the whole code and understand it.