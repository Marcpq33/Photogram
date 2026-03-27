import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("photogram.android.library")
            // Shared feature dependencies (Navigation, ViewModel, :core:model,
            // :core:ui, :core:designsystem) are added in Milestone 1C when the
            // feature module graph is wired.
        }
    }
}
