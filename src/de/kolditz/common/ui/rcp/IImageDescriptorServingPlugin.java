/**
 * created 08.11.2011 15:52:16
 */
package de.kolditz.common.ui.rcp;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * This denotes an eclipse plugin instance that offers a method to get the image descriptor
 * for the given image path.
 * 
 * @author <a href="mailto:Till.Kolditz@GoogleMail.com">Till Kolditz &lt;Till.Kolditz@GoogleMail.com&gt;</a>
 * 
 */
public interface IImageDescriptorServingPlugin
{
    /**
     * @param path the path to the image file
     * @return the image file's {@link ImageDescriptor}
     */
    ImageDescriptor getImageDescriptor(String path);
}
