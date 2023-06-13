package com.capstone.sopanfinder.view.result


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.capstone.sopanfinder.R
import com.capstone.sopanfinder.databinding.ActivityResultBinding
import com.capstone.sopanfinder.view.ViewModelFactory
import com.capstone.sopanfinder.view.favorite.FavoriteActivity
import com.capstone.sopanfinder.view.favorite.FavoritePopup
import com.capstone.sopanfinder.view.profile.ProfileActivity

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
//    private var flag : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.ab_gradient))
        supportActionBar?.setElevation(0F)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(Html.fromHtml("<font color=\"transparent\">" + "" + "</font>"))

        setResult()
    }

    private fun setResult(){
        val id = intent.getIntExtra("id", 0)
        val result = intent.getStringExtra("result").toString()
        val name = intent.getStringExtra("name").toString()
        val cell = intent.getStringExtra("cell").toString()
        val power = intent.getStringExtra("power").toString()
        val efficiency = intent.getStringExtra("efficiency").toString()
        val dimension = intent.getStringExtra("dimension").toString()
        val weight = intent.getStringExtra("weight").toString()
        val link = intent.getStringExtra("link").toString()
        val linkImg = intent.getStringExtra("linkImg").toString()

        binding.tvSopanName.text = name
        binding.tvCellType.text = "Solar Cell Type: $cell"
        binding.tvPowerOutput.text = "Power Output: $power"
        binding.tvEfficiency.text = "Efficiency: $efficiency"
        binding.tvDimensions.text = "Dimension: >$dimension"
        binding.tvWeight.text = "Weight: $weight"

        Glide.with(this).load(linkImg).into(binding.ivSopanPic);

        binding.commerceBtn.setOnClickListener{
            val uri: Uri = Uri.parse(link) // missing 'http://' will cause crashed
            val linkIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(linkIntent)
        }

        val resultViewModel = obtainViewModel(this@ResultActivity)
        setFavoriteFlag(resultViewModel, id, result, name, cell, power, efficiency, dimension, weight, link, linkImg)
    }

    private fun setFavoriteFlag(
        resultViewModel: ResultViewModel,
        id: Int,
        result: String,
        nameSopan: String,
        cellType: String,
        powerOutput: String,
        efficiency: String,
        weight: String, dimensions: String,
        link: String, linkImg: String
    ) {
        var flag = 0
//        CoroutineScope(Dispatchers.IO).launch {
//            val count = resultViewModel.checkSopan(nameSopan)
//
//            withContext(Dispatchers.Main) {
//                if (count == nameSopan) {
//                    flag = 1
//                    binding.favoriteBtn.setImageResource(R.drawable.ic_baseline_favorite_24)
//                } else {
//                    flag = 0
//                    binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_border_24)
//                }
//            }
//        }

//        binding.favoriteBtn.setOnClickListener {
//            CoroutineScope(Dispatchers.IO).launch {
//                val count = resultViewModel.checkSopan(nameSopan)
//
//                withContext(Dispatchers.Main) {
//                    if (count != nameSopan) {
//                        resultViewModel.insertFavorite(
//                            id,
//                            result,
//                            nameSopan,
//                            cellType,
//                            powerOutput,
//                            efficiency,
//                            dimensions,
//                            weight,
//                            link,
//                            linkImg
//                        )
//                        binding.favoriteBtn.setImageResource(R.drawable.ic_baseline_favorite_24)
//                        flag = 1
//
//                        val intent = Intent(this@ResultActivity, FavoritePopup::class.java)
//                        intent.putExtra("popuptext", "Favorite successfully saved")
//                        startActivity(intent)
//                    } else if (count == nameSopan) {
//                        resultViewModel.removeFavorite(nameSopan)
//                        binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_border_24)
//                        flag = 0
//
//                        val intent = Intent(this@ResultActivity, FavoritePopup::class.java)
//                        intent.putExtra("popuptext", "Favorite has been removed")
//                        startActivity(intent)
//                    }
//                }
//            }
//        }

        binding.favoriteBtn.setOnClickListener {
            if (flag == 0) {
                resultViewModel.insertFavorite(id, result, nameSopan, cellType, powerOutput, efficiency, dimensions, weight, link, linkImg)
                binding.favoriteBtn.setImageResource(R.drawable.ic_baseline_favorite_24)
                flag = 1

                val intent = Intent(this@ResultActivity, FavoritePopup::class.java)
                intent.putExtra("popuptext", "Favorite successfully saved")
                startActivity(intent)
            } else if (flag == 1) {
                resultViewModel.removeFavorite(nameSopan)
                binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_border_24)
                flag = 0

                val intent = Intent(this@ResultActivity, FavoritePopup::class.java)
                intent.putExtra("popuptext", "Favorite has been removed")
                startActivity(intent)
            }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): ResultViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[ResultViewModel::class.java]
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite_menu -> startActivity(Intent(this@ResultActivity, FavoriteActivity::class.java))
            R.id.profile_menu -> startActivity(Intent(this@ResultActivity, ProfileActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        finish()
    }

    override fun onResume(){
        super.onResume()
        setResult()
    }

    companion object {
        const val TAG = "ResultActivity"
    }
}