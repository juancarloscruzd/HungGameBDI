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

		IComponentIdentifier player2 = managementService
				.createComponent("player2", "pe.edu.utec.hung.bdi.Player2BDI.class", null).getFirstResult();

		CreationInfo additionalInfo = new CreationInfo(
				SUtil.createHashMap(new String[] { "player2" }, new Object[] { player2 }));
		managementService.createComponent("player1", "pe.edu.utec.hung.bdi.Player1BDI.class", additionalInfo)
				.getFirstResult();
	}
}