package com.example.lock

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private fun runRootCommand(command: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            process.waitFor()
        } catch (e: Exception) {
            Toast.makeText(this, "Root command failed: $command", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fullLockBtn = findViewById<ImageButton>(R.id.imageButton)
        val unlockBtn = findViewById<ImageButton>(R.id.imageButton2)
        val adBlockSwitch = findViewById<Switch>(R.id.switch1)
        val micCameraSwitch = findViewById<Switch>(R.id.switch2)
        val networkCutoffSwitch = findViewById<Switch>(R.id.switch3)

        fullLockBtn.setOnClickListener {
            runRootCommand("lockdown enable")
            Toast.makeText(this, "Full Lockdown Enabled", Toast.LENGTH_SHORT).show()
        }

        unlockBtn.setOnClickListener {
            runRootCommand("lockdown disable")
            Toast.makeText(this, "Lockdown Disabled", Toast.LENGTH_SHORT).show()
        }

        adBlockSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                runRootCommand("sh /data/adb/modules/lockdown/adblock.sh")
                Toast.makeText(this, "Ad Block Enabled", Toast.LENGTH_SHORT).show()
            } else {
                runRootCommand("mount -o bind /data/adb/modules/lockdown/hosts.original /system/etc/hosts")
                Toast.makeText(this, "Ad Block Reverted", Toast.LENGTH_SHORT).show()
            }
        }

        micCameraSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                runRootCommand("lockdown enable && lockdown revert")
                Toast.makeText(this, "Mic & Camera Locked", Toast.LENGTH_SHORT).show()
            } else {
                runRootCommand("lockdown revert")
                Toast.makeText(this, "Mic & Camera Restored", Toast.LENGTH_SHORT).show()
            }
        }

        networkCutoffSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                runRootCommand("sh /data/adb/modules/lockdown/blockinternet.sh")
                Toast.makeText(this, "Internet Disabled", Toast.LENGTH_SHORT).show()
            } else {
                runRootCommand("sh /data/adb/modules/lockdown/unblockinternet.sh")
                Toast.makeText(this, "Internet Restored", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
