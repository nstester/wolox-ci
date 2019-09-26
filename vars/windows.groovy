//@Library('wolox-ci')
import com.wolox.*;
import com.wolox.steps.Step;

def call(ProjectConfiguration projectConfig) {
  return {
    env.WKSPACE=env.WORKSPACE.replaceAll('\\\\','/')

    // create archive
    bat returnStdout: true, script: "if exist archive (rmdir archive)"
    bat returnStdout: true, script: "mkdir archive"

    List<Step> stepsA = projectConfig.steps.steps
    stepsA.each { step ->
      stage(step.name) {
        timeout(time: projectConfig.timeout) {
          withEnv(projectConfig.environment) {
            def echoscript = ""
            step.commands.each { 
                echoscript = echoscript + "echo + \"${it}\"\n" + "${it}\n" 
            }
            writeFile file: "$WORKSPACE\\icl-pipeline.sh", text: """$echoscript"""
            env.FILEPATH="$env.WKSPACE/icl-pipeline.sh"
            echo bat (returnStdout:true, script: """r:\\u4win\\u4w_ksh.bat /c %FILEPATH% """)
            bat returnStdout: true, script: 'del %WORKSPACE%\\icl-pipeline.sh'
          }
        }
      }
    }
  }

  archiveArtifacts artifacts: 'archive/', allowEmptyArchive: true
}
