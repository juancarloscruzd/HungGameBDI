package pe.edu.utec.hung;

import javax.swing.SwingUtilities;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import pe.edu.utec.hung.ui.FrameView;

public class Main {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				FrameView.init();
			}
		});

		IExternalAccess platform = Starter.createPlatform(PlatformConfiguration.getDefaultNoGui()).get();
		IComponentManagementService managementService = SServiceProvider
				.getService(platform, IComponentManagementService.class).get();

		// Guesser component
		// Create the guesser component
		IComponentIdentifier guesser = managementService
				.createComponent("guesser", "pe.edu.utec.hung.bdi.Player2BDI.class", null).getFirstResult();

		// Hangman component
		// Add the gueser reference into the Hangman component creation
		CreationInfo additionalInfo = new CreationInfo(
				SUtil.createHashMap(new String[] { "guesser" }, new Object[] { guesser }));
		IComponentIdentifier hangman = managementService
				.createComponent("hangman", "pe.edu.utec.hung.bdi.Player1BDI.class", additionalInfo).getFirstResult();
		;
	}
}