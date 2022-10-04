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

	// Integrantes de grupo:
	// - Anchiraico Orozco, Maro Antonio
	// - Cruz Diaz, Juan Carlos

	// Notas:
	// - Se agrega la carpeta resources por el uso de imagenes y el archivo del
	// corpus
	// - El método de adivinación está basado en probabilidades de ocurrencia de las
	// letras con respecto al corpus
	// - Se usa una lista linkeada para seleccionar las letras con mayor
	// probabilidad, además se intercala los métodes poll (85%) y pollLast (15%)
	// para reducir el riesgo de perder intentos
	// - Se usa un Frame para mostrar en imágenes el estado del juego

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