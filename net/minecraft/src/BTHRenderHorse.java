// Devon 'DNoved1' Cooper (dnoved1@aim.com) (Totally not copied from anyone :P)
// For Better Than Horses
// 2013-06-14 (v0.1)
//
//A note on my variable names:
//--variables starting with a c are Constants
//--variables starting with an a are Arguments
//--variables starting with an l are Local variables

package net.minecraft.src;

public class BTHRenderHorse extends RenderLiving
{
    //This is just a barebones render class. If anything else needs to be rendered onto the horse (say...fat rolls) it should probably be put in here

    public BTHRenderHorse(ModelBase aModel)
    {
        super(aModel, 0.7F); //0.7F is the shadow size. Feel free to adjust it if it feels off
    }
}
