/*This file is part of GiftPost.

    GiftPost is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GiftPost is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GiftPost.  If not, see <http://www.gnu.org/licenses/>.*/
package com.Balor.AutoSave;

import com.mast3rplan.alphachest.acChestManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class AutoSaveThread extends Thread {
	private int wait;
	private acChestManager toBeSaved;
	private boolean stop = false;

	public AutoSaveThread(acChestManager m) {
		this.wait = 10 * 1000 * 60;
		this.toBeSaved = m;
	}

	@Override
	public void run() {
		boolean fin = false;
		while (!fin) {
			try {
				toBeSaved.save();
				synchronized (this) {
					Thread.yield();
					fin = this.stop;
				}
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void stopIt() {
		this.stop = true;
		toBeSaved.save();
	}

}
