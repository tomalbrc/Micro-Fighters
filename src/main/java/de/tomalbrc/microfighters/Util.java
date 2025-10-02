package de.tomalbrc.microfighters;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.item.DyeColor;

import java.util.Map;

public class Util {
    public static GameProfile modifyProfileForColor(DyeColor color, GameProfile gameprofile) {
        Map<String, Property> props = new Object2ObjectOpenHashMap<>();
        switch (color) {
            case DyeColor.GRAY: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTMzODg2NTg4NSwKICAicHJvZmlsZUlkIiA6ICJlOGIxMDU4NWQxODE0MjBhYjkyYTNjYjIwMmY5OTQzOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVHYXJtaWsiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODA2ZWVjY2JkNzNmZGM1NDBkYTM1NzBhNzkwYTU5YzI5NTczMTE2MWE2OWNlNTA5ZDZmZmRjMDI1MGE3NDA2NSIKICAgIH0KICB9Cn0=",
                        "ppvuVaZzs/UFJTtxqAQgONOLLFqLe2Py+Qr+DUrCXrT3f9qYo9Z/UtIvRTQgLYTSBc5706unc8IOaA7uzynteE0cgDcnZtzQUafrWxygk99s/fcE14ToxomJe8iTuRXP0mVmi9nRKYiZMwv6o/9XRo/RF99kCg9ay4aLBSkCZcMGuoFjASsC6W7nQ8Hm5OQBAJ3Rb9cxr534/5Yt1KOabzFeuIseZPVlANAIO4tbVmVyoEuaH1B4x0YF6hovCBAg08BgVVNgaaSFF+SbjJqogC0PLEW90xK6Xs8zDqyjt864a5AQzgO8wLMiJg+kEnN2k+AWyqgpzg6PnQczpwdnS3gPMm9qDv/RRPB/guJHbjNHgxw76AZ8cH2rloiLyI9YdArDhbnEtXtui7/6NpF4XEJkq6BraH4EmzGejwnfjBFfOUL7xR7jvrlB757unKbXz2gc4hNd5JL1ydMID5PrzG97oqPpFo9tcx3Nm5oGeOBZRh3JU1uVjrCXbgySRIF1xgXdHA8BFUoK69Xl/nFturTqni8biCCggNpdYXhu4PM7sCiXk4YVxPtn4rI/tAC7bioDia+462mGy+11u2Auo8EJ/ymF2PJT6aAL2UbwX3QH2c11tP5dUuL/j63EYw6N92ugdWRTZz6DXOh2HT5aepD3iBuSV+vGDQ2ONeSyDKY="
                ));
                break;
            }
            case DyeColor.BLACK: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTI4Nzc2MDc3MSwKICAicHJvZmlsZUlkIiA6ICIwZGM5ZGY4ZjlmNWE0NWQ4ODg0ODkyNjJhNDM1N2FlMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTaXJoZXJvYnJpbmUyMyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82NjNiMDQ1YmY1NWZiODY5ZjEzYTgxOTEwMDMzOGE5NzVmNDFmNTI2ZjJmODFiZTA3YTY4ZDc1ZDQyYjFiMTk4IgogICAgfQogIH0KfQ==",
                        "Yy6HQeMQagUydLP+cwovgZGj9XS3vqCyM0uy4yED7H8zRb8X4ZqwltgF7u++DQxRlV4k30wJSO4wnP5Au/fQOJ/9B5RByooYpZeMY6JFIcKtJf41VGEZ3nyBUKIxqOEjbjs90zhy9iTvhY1s7lJMMmsf22tz4A/RY04UBKhhJHfZFULxHLh8AD70fniNod/gWor0VD2jNEPql8AfvEFSawDiQ0l1OxO+tDYyy0W23lijyb4NA7hqPYjScmSrWoWB7b3IuziXkPCIhlccQheadZmVzeSDbxmeN7/RTyyuM0FgRQRBAs5EmsMDQ9jA2rZ4hZW35Uy7ksQYx/iHm2ugDU4rRIVpu8PhyXnjFDFdOu5/2kjEZe3J4tkCejUd5puFlMbJmNNIJ7r6WzJz5T4hEjs45Cy31w7EqhVoF/gcnOgy7LMEU5keuFYT48B/bMZYFKvWgOXbpWJcU3yQOzcNg4cu+LxXD1/OaSq658X5oMdwirEN5a8b5j+LowNePy3GtON4vE107maq1x+wIUWSUQfx88r9ByMQ7COqttKgjXGulIVhhAfjz8VzgBwwIqopy1JOO9vV1U5quIK8EfJdHXv6txSJUajJHWlwubBJRKssJTswCA0BrRZ8O+kTgMHas/C0CFTBL7I1BcN8cOJWZQ6DfT8Js70DUFVDCbYd4aY="
                ));
                break;
            }
            case DyeColor.BLUE: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTA0MTE2NDU3MywKICAicHJvZmlsZUlkIiA6ICI2MTZiODhkNDMwNzM0ZTM3OWM3NDc1ODdlZTJkNzlmZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJKZWxseUZpbiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mZTUzYjE5MzI5NjRhMzY2MmIxYzkwMGI3Yjc3YjBmYjI3NWYyOTRiMGJiNjlkYzRlNzViMGEyZThhYTNjODcwIgogICAgfQogIH0KfQ==",
                        "GmPqlDTt+O2mbOzZunNGGKkbuhNpow2ILOsYVcn0kDn0AMyzSDUJThpJcd7JTrjdNCF3fNDVUoKcyDZUK4lSnjB+vkUsFljZSpNfd+jVjdInPV/uBOvqKbCkV9HcBea2Vv1mJMFVf2hFDBIXAlJprjOFNqM4RMjmebLl1yCnwSvTjlzNuAew++9v0zst3hwHlKfAdM8oZ1MM7fo5oi8RPJc0r1H+pazR0xX/e3o1o3mlNGlI61AfbAFidvRC9IG4DK9u+BvJjiDXipP74DHMjQFWDYlpRtrCGIYxVJp4HyPUMJKEhFoXxuauEi6HRaBbeX1N1vkWAZfhKlYEDpJ93rGFrmyvzhBk0tkYm7q/cTlZ0ofj5kEzoU0nTjMVZpKkZE+Ts3rTUTE+dy5toL75M03HnLhwBeoVKBzOwvxQP4EAoku3o8XK1LV3T8djMXNDTu6h7acM7qOeClJ5kE5r1/C6ht5+3bOSc0gZnLoQF72YaZ019a7gXNA6Q3NpMV56GPRadPl6in5TzGzXKoJnYBhR0U+N8wb4fcsq4YkUtcAu14sOGE7AinI71bwx374erhwtGE+8Dze8lD6KubKS86xzVgAeE5xXTz+PWyWrGBSUyoS628QWca316MGnnpZC/GstndyVd1/35tq0RJwKqkJOFJ4gV9iuVxzsyFYCBys="
                ));
                break;
            }
            case DyeColor.LIGHT_BLUE: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTI4NzcyMjUwNiwKICAicHJvZmlsZUlkIiA6ICJlOThhZTBlMTI5MDg0ZDA5OTk0MTg4N2Q2YTk0ZTI2NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUYXVuYWhpWmVhbG90Qm90IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M0MWNmMGU1YjdjZDk5MWY2YjZmMmRlMmI5MGEwMGQxMWY2ODcwM2YyNmJjNDUyNjE3YzRlYTVmMWRhNzdjMGEiCiAgICB9CiAgfQp9",
                        "ULBRlYSSpZyczoFR+FaGDs4tTA9u3dhWJL4eKvBem84FC+JEDnpUNqZjFOwDZsMPzSJGYcCpm635BeQCxGO61KYbD+qR5OtAjojyNBr35nKM19GMRU/furzoEI2YhNH+RhGNqXQHUC6c8JhcUh4t2Sq7kqJTo+rTARpeAb53jMYUDXzrjISKzHwDvvAzPEh26FA+A2KYgwrGPMgmZPnR4LQgkLwEz4ZEXUUxdHsQ/YNwRuu4w3gFp+O9HemC7NHE8zoeo95dcmqUtZMUD9STM+SugXi0mZ2Jf4pPptSXwBUkYrIL47kcujZr4+/gfp1ML7bqsY062h/Pqk/IOfYQ5HlINNaggGYdnYcVv/X6YOMupAhZcbYVEKOuA75W+CwetDrnym1YcEafdvQHA/q1KsED2TUHjBGU+G5Eqyr9L0q/XGPpWHjEkSWaRmf2CAnBFHe60IO2iPHZEb8Dq65V72VeNZ79N2JyKlS3/7+ccTllte8L8xxh8b4LYt3MH4k3f/7qazUn6uz/m+to1nQeb151QbWyk5yJeWbf0gtdwwMk1ntiPN/s19ZK19rRBfHTD5YdYp4wk6eXRYnWS3nWNi57eYLebzLhhm1k9cBa1I8e4mxO1n1VrWrx4vVd1Qaq3vWm7fWZgwvz2HR6LAg5XHoy371obNTxLjtdQgGhZj0="
                ));
                break;
            }
            case DyeColor.GREEN: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTI4NzY5NDM3OSwKICAicHJvZmlsZUlkIiA6ICIzZmM3ZmRmOTM5NjM0YzQxOTExOTliYTNmN2NjM2ZlZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJZZWxlaGEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM0Y2VmYzE1YjFhZjdjYmMzOWY3YWU5MTNmNGI1NzI0YjM3ZTYxZjBlNDkyOTdjN2Q5NmJjMGMxMGRkMzA1NCIKICAgIH0KICB9Cn0=",
                        "tT0QSLT5D4ANBmcvpjMhJCerwtv2R9m4skMNOMuGGV8bV/6IT0OjlU98MkbN1KrXDBoPNk8d8bCc4FhPMGG6aBWSwu9BxACqe0sBOoEv3HlDQoWihxIhcMrqLYHCnO45jT/pu0noPWQMOlmXo5EjQke7r/vHmA+rVI2yV1ihAg5E1NhRk/3nPq02Jh52e35vIzigzfqin+ODdtwN6DbPSfAtw7f6bPhnq0hCChhQ/qQdS2nRFB/BCWoPUgWpZysMTL5zVYuddrzsl0bxpaZ+Cxb5x1SnDmn/A309Qaq5uXF1Wgp2dvNF7N6jsZU5ttAoBubwFU57omAPNDQSKGREUUyUtRcOsUiILKD0w6lSr9nb8RSvrXDk87iP6ozR75s+FrZFs3vrbHHcmcKrEGaUZVDizm/ggWXP/0YM3BiaNa28Raqp7eY1S9AntrbHeRsDsLmBCjqIXVdfTZIIgBLSkKF0EAr/6J4vpeTosxARh/JCRlKMg584c4Or3EwknNEszP4TRlOPZFdfUF9is7ayRuCl3eJ5Cb7b24Rgn3J/WHNHQfuR2MbvNQm/8A/s/dVyaiemnogu3GLDKWtZggOJ0UT47s/EeiBzxVhV2N0arayoUBwavAtZDPuU+IXyBmC9HIuZ/WclB76y4RcV3+tFyvY47H471nYIE+dMhoES+0c="
                ));
                break;
            }
            case DyeColor.LIME: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTI4NzU5MDE4MywKICAicHJvZmlsZUlkIiA6ICJjYmYxNGIxMGJhNWU0NzgwYjIyNmFiNmQzOTUxODk4YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJFZ2d5QnV0dG9uMjQxMSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84MjBiYTVjNTkxZTkwMjNkMTYyMmZmOGFjODVlYjE0ZGUyNjZiOGE0YjljZWQzMGJmZTdlN2NlZmU5MGQxNjYiCiAgICB9CiAgfQp9",
                        "moNY7n6OrqgzvWJFnsQ0eTe5dNknS9EnABdMohpt99hC00uXtCv6Y4W7QdDK+hSFR28SX6SwGUiGAzW9MnaaaFpmF921e/PZWCbt2ln52c2nIfJ737K7f+xMNU/sV2068OjnJ9btuJX1XYe/0Wan5BDGZU++Sl0JCJtg74IiXvxaOmhRP/wkCq+u118zQzWSyklIUF7qlrF3d5M+Pq6pHbH2v/Z4GGTKywiEDdxzoOwIUjZ4u1h2tR5kx5vppy86a0dIEXAmxf6yuzjVRxKLCsmTtaJU+afn7uJmgh5poHcZmNZPHBXufKI3sbFaleULR38fkc3ARrgDSedPzBudSC22aqHaNZOGCFCksLp9IPsKeykf9qaI9+zgSTpbWvcnN5hj/q+JPyOEKGC/0jCDdIk4YzCg6fvz2mwh5zyjr55eerQBuklpMWoi60ZvRvXjl02rGlX+wcpI6dXaQhLRmz+hx3FuqxwZ+ILIacNlHefKvE/9/oN+oE38DQXjhNYZlJ6NoEBGtqi13QMXzs5DsJVSFzlVJbNTPwXRzqJ+vCBCd872oyczruUCEivyFtCq0LTo458rFqCLgNDoWSrT1e6B7iInWfis5+H+IAhDmQRiCEZ5QQIzvCIz+/4qeyIZftPwLJFmWyy2TOzFlq9yJ08Q38hSYQTTYR3ZOinegeU="
                ));
                break;
            }
            case DyeColor.BROWN: { // OK..?
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTA0MTIzNjUyMiwKICAicHJvZmlsZUlkIiA6ICI4NDIyMDRlNjY4ODA0ODdiYWU1YTA1OTUzOTRmOTk0OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJuZWE4OW8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVjMmFjM2UxMWYyMTk0MmVkMTE5OTY1ZDM3YTUzNGRhMmI4Y2Q0MGQ0ZWM1N2FlZDRjMjBlOWU0NTJjNjZiIgogICAgfQogIH0KfQ==",
                        "DmNIEKZH1SbnBmeBROE35+YsnZ3CVEMNQcmmXtlH7tDLmrxLGpV5hetTYWp4+xjJ/mgDieS12MILrxic/xmHjm7wDUwMpxt8qIApQtKr+WTuHkOO3CVkrgBwtyEWtvuQAmQo7o1iPf4iYPOa9pwLsMgT29zHOfbn9nJ0WldajXx6vLoSRIheCVQ4x3pX5RIFoLRlXmD3n2dgsZnIuQeulvdhUH3egFV4eWkHBnrW/1H0wog3GKhJb7CJN8tZOLcayEiZMDYA3C8t/MAwBRyXsOFFwXQ46z9yFRnVuNO4yv/uX73nnvXxpqyQlegdcmuDdnHr9pwbTzexhj6y2/uUtM4HSLeyCvQDpzf46eDHNRRuT3Rm4TOgb/q7sQ4gOsVMslIVFESoThNyOH8L5ISsJGJP0nMIova19WxSKwohThUFYjmvndMSBj5YBhLtg1uQ6ZcmiuGNF52psoWmphqzJODX7W6g7klQAK6m2/18dHsLXOWY0dvL+4O0bbD9dnd8JERAGvPQ4orn5UbG7tXaa4GoxGSLWrtHjtw3hZJ9FWHPgCqFh8A9YmZaTVZk1XK6yBC6mFNfn5IkcTniteVy3T/TvFlkHxgJJKkyl0I/e6XFZxYsDv9b+PrkVcpAjy7mEt8blviffff3a0+BPOg3qtChAuHP9htECHnM975olPc="
                ));
                break;
            }
            case DyeColor.CYAN: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTA0MTA3MzI0MSwKICAicHJvZmlsZUlkIiA6ICIyNGFjYWZhMmVlNDA0ZGMwYmRjMjViNWVjNjgxZmRjZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJXaWxsZXJtaW5hRDM0dGgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWExZGY5NDVkNmI4MmRlOTI1NWQ2NTYyNDgzZmRjMDM3ZTM2NjYxZDllZGYzMTJmOTY5MTk3ZDhiNjI5MjAzMyIKICAgIH0KICB9Cn0=",
                        "RJdqR+gKteBK08zDauhOwlNta2ivJMAn58Z5RublhoP8wGrMQMKY0GQSKVlc1XFLTLG5xwd45VYkazTGcADDvNItV/93lENr8lEhJZUs53/F4TfUYbfoBQZksoimIvFZIJ4Ma0Wz+WJUlo8jwLLiqZEFPeErYSCI1GPl50voCmWGezKKyFdBYCTM6NL1vSm3qfG1ar3WirVmRjxIst+W8T5pXUOniPG7XuTrZ0LtTLfog9F6RgkYlqDmwWKuK1Pqmfn3VJ1Q8QZ0i9n6/TzvuFX/Nk4YhTytc7r271Qajh4Bmv/JrFT3wpxl+wzMOD/Yiun3Uij5xF1f5YqzTj6bIVCjD+S3PnZkZ/j5XHduGVlYFWZu5mLDfjBlQFGy2zdWAwibz9UdUxDdTR20oW8Eifnh+ZqzaOGjOhsIoN2IZNNC52u3FkADpzpoCIbuPuHnxDMzTi2kOyi5KnyqgcO9R6qnc8BzbYhyx6xJVN4KqiRLi/W74oZBdiXNWu3AbSRnx540ryQpyOGb/fX+i92pbVvpljyzQ0qXUXgRBQkLt7g8I/DciLEctaXIL0dAVPvmunVYQQ0Vf3E3Q6L64GNo7cUY2HIvQgliKsCdyczblFIYskcdx/uwwLiCGYag7fVOFYKSg6mcfr12TkLn4xOqJof0fku5i14m4sfRc6MF0oQ="
                ));
                break;
            }
            case DyeColor.ORANGE: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTI4NzUyMTc5NCwKICAicHJvZmlsZUlkIiA6ICI4ZDYwNGY0NWM0OWQ0YWE2Yjc0MjhiNTJlYzcyYjliNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdGFyRG9ubiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yNGZmY2Q1ZDZjYzRmOGYyYjZiZWIxZDI4NzgxZDU4ZmNiZGFhYWYyZjFhZjA0NTY2OWQwMDE1MzIwZTEzMWY1IgogICAgfQogIH0KfQ==",
                        "iNCbwwaaoMv6uPYr4pyZKEnw3nRZ871+aQpi24q+mDrAObb28S+eEheEBiImwO3fG2fl5Dihdvvr6PjxMWyqoJyBmaqNXRl9SbAWb1GGtsnOe1Qv/0yyUrapgX+OMm4txGyEMy/IXV0DN02AWFOiJ2ijBuc5Tcw4zhXnlCI+RHsyhwVNM7oKVqketgz2eOnsEzmypR4oqmraic3ce2fuoP9cQHkLzbwd153uz1HwB3aR5T4RvwlrMdBIWclk6sQ+HuBdzLo2fz9Qicw2RBnTzMGKwpb+y0xk5TDU1CRz2dWW0AqsPVZBmCmE0u+LDBMJsr4dMelLgcJs4y01Xw87DoqJuvggg+rzpcJ5i5lWGqwm2os5cOaotH/K9j7Ly/Cg5RgXKgLZJYOzDQuHZZeT1EbkhkkfqL9W3sjjfkHOq7x2/eDTVOzh+Q4EOXyijMfnZVyreN2HdF6N+aot8N09smiZdGtcY6oXdU7R87FaSwnA2epFCEh+JQyjia5LhkmnvmFjjFugzqQka0qrZ9wi2Q2y0VGH35s5zPW5Kg6XbDnJv4IVhW1nXqycZxZ7twVxeo8JIocVdPUE9XgHUBTp7iLysGFTvYExelwzX5ZVM4VWFRlY122+NQuXm1f2eL4a/9zwsV+N2ADnvsFYIYMUslo0mqELs5GdyQbgp7YQO/8="
                ));
                break;
            }
            case DyeColor.YELLOW: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTI4NzQ2NzQ5MywKICAicHJvZmlsZUlkIiA6ICI0NTk3YjBiNWZlZGY0MTg0YjI2YWU0NWZjYmVhODVmMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQc3lrb19EYXJrIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzI0N2MxZWE5NzRmYTBmYjU4Zjg5NWU0ZjVlMzQzNGU5ZDMyYjJlZjMxOWI1ZGZiMDVkNWM5NTg3ODIwOTYwMDUiCiAgICB9CiAgfQp9",
                        "QR9aceBeX9Hix+ZZzflpaw3jFJxh54aoCxULmtETWiDr6gKLYGCvazubyKyBU4F8/4+wvmYaXi/iGHR7xn+CUQqw21bEhTzrh9j8tKeHEDR7Dlh5ce/cxuaifgpXxNjQZ3iAsWrzsFS3Ssb7mEpKr/0ALlitFjV0ZNwZCKOSX9+XC/7QqXJ61bUWs7aI5MnwYwnqs7wFPnu14uRECuCzRU9CD1DpW/Mr3uK+/tR6cSakMIJ+X3DtAbuWqj0fITUHHZ+FwSogXF7qxRv2NTMYMxXrhuJsGAGQH+LKzHsDivPH2vzfkr8Dj7a+sHeQZXHz2af2iP8IDvRcA8/iyXkkFIGpJOy9LQLg0ADgTXqxxaY0P3nsBgfOO7glutX8xhblu7dmtkJnFdoC0Drtc23+MCXt/G9UeQy4wN45zUXr6cb7pKazuviKKA4QI9m+dn02uSNVOMHn57sNOUWuUE1OMqO+Do05rWm+QLAgbDsg+0rHEhdKUT1uolT90QtQZv7Kd2hKRMuWCMxN6UfnxJb+Cn0G+ggqMBqdGZzpzzDrYEkraKYI77f/W9fBHrTUq+I2GWZmnfeweZQGJ+0dmOv3PnGeMqBQXYGtsXlzGaBhDTXqr+uD/RF+s3GSG8u8vhOxRvEUGrTawricNkUkApV/PlXhgy3uCjcb9aGp2h5a1tE="
                ));
                break;
            }
            case DyeColor.LIGHT_GRAY: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTI4NzQxMDUxMywKICAicHJvZmlsZUlkIiA6ICIzZDU1OGQ3Y2NmZjk0ODdkYWE1MzhkMjM4NGE3OWFkZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDcnlwdGljTG9zZXIxMyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yMTgzMGM2NWY4OWJlNGFiMTAwZWQyMDgzYjY2MTQ4OWQ4ZmYxZmMyMjIxNzQ2ZTliYTNiNTNhYmEyMmY3MGExIgogICAgfQogIH0KfQ==",
                        "G163t4IguqSL+SQ/CX/YyZugeKWSPdXZek+HVJhz98uxhy3oclcrYtH/rmyASC5MiWemIwRRxboa4hXA05uesKqa7CwtJNQgZDEE8AbqfWbQ9RymfzunjXswVN7qdsrmpBIAuPLjBN8mIqLrOSwPcV+kZ7YmGNQqmdM+h05xSBIXA5QW+XFl6X+WGWb5XIWVUyJMBu+j8N6XwAnW4BM969haqmH6PghSxULpBpZ7+Sct71/VLMvUFw6IFcDAmDBErSN5I9iH4ZsZFfD0YCN1wc9x5EBzeIE1MFJdD3YJFX9ovdz2LJRN/aq/HwXSViIhiH3tVm/HgZcTD6I9dBz30pxaMwzPtldWZhXGO3eTf1i7DFRW0d00UFfyb/3zPMvROcFLgh2HfS4vS7nDANrFSIHh5868nQfVNCSp5yvxsFL0EQQEmqSgjn5/V6uWShmiX6YA8XCjSxwUNCmq6MJLZZd8bL2czjdY7gBfFKtkk5iWNO1uB1jVauFybFOCslcSdkxphXxqKfbSVcH4HLQgeyiKmVDddA7LqwC+0n6nDYQZmtGl4dbOBTmH+7yNb3GWS0+ZJqK/WD1eSL9B2gX5igd8b/mgOgcfZkHPQmBcAvzBpVwWHv3USnuSAVIDPQqH4e1SWJHvK4jtIKkFPtC9WphiGNwJHcGFKig5Skl4KLs="
                ));
                break;
            }
            case DyeColor.WHITE: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTI4NzM3NTk5MywKICAicHJvZmlsZUlkIiA6ICI5OGQxYTQyNmRlMmU0NjBkYjdjNWExMmY5MGNhODg0OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJLdWJpbm9TSyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85ZTJlODYxMTQ0NTkxMmRiMGMyN2ZlNWNmNzZiNDg0ZjUwYzNkMjgyODg4MjY5ZTZkYTFjOGYxZTcwYjE4ZjBmIgogICAgfQogIH0KfQ==",
                        "EK3uGchwb7vDffrbjjksrt2VyqJJJwnvdSVG19y+8LPnNU4cin4dLyYa3/sIXvKB1iozv94VNqenHaTi8rF0QXMSPprjpu4nzfTuCHKRQljl1IEa2siHJN2pLInqeScDu+MurhLGqV97GsbBRlBH3JsI/4SM1uvQ2+12kSlIYNTRgUoRgjIoSE3N8DJeSGxkCrtLznEK+MS+5SzUuWaktaTxS9ouaJaAAhSBD5we8zD3vJDLQlopb2EUnLiCpeOEJBMF/1hSDKgOaRG9jLoajzUKWUAZrVFLDM3KQR7XCQpVYuzW/U2E65oAtw1XNHpuhe9O4X5ZTA5Yg+BYzPeacgZ5jo50bjSRXGwJnNrjAyVldLbTJXyiX5vJXt/eB5YJiXzJdAU8f65vJM4RmpLVrl8zVqaDO0jGkSpXtiLwxFgKTFujSwYAZHjgpdIb7q6ZqdLdApD4C6YWon4/u595N3Oo0oPEwm7R8S+Zew56mI1WR0qyXwF6gT86XdXjyLh4gPHa/Lrad4gSAi7FamzrYlK3Yc9e/x6hflVWRP8r+cgeeYUuu56sinjrcDD4IC/oEVzQBCaMvg0HMX8/wF/Wu73nQTA7Y2H54xDfUDy1BE4+8LpipOBG8biw+apw4mDk4Ory6Y0NE6H9asxZ8Up+7RwMMeN5fmNilzU0zIrzGSM="
                ));
                break;
            }
            case DyeColor.MAGENTA: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTI4NzMyMTMyOSwKICAicHJvZmlsZUlkIiA6ICJiNzRlYjViMTc5OTc0YzZjODk3ZTgwNTM4Y2M1NmYwMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQYW5kYUNoYW4yOCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xZjc3NjFjZDlkMzY5YTQxYzFlZDkxMDc1YTBkOGNhZTRlMTFjYmE0NjM0NzgzMjczOTJiMDg2MGIxOGZhYzQiCiAgICB9CiAgfQp9",
                        "WCa/NK8RTnonM31cqTPkYhl5Fz/BJs4ScrizdGudqc8FBKRanNYD6wyAv0X+gIhWu8Qo2w5/8CeNscSRuvmnoIa+7qIm8GcqNH5EMWvn0Bj8OO4nR9nDiaqscaHfDK4U+HGlqW2TFkdu0DECNwOliU68fD928Xp9ecLFu2rplbXYx1g1+k4OvGNNYXswyuLR8UDT34jEI8ax7nxg8rgrX5I3WKVaezesMzhlZ9dO1AEhuU8K+PX3lBhi9njt+byqdSm7Dvnc+gGpjCOVNCC7oSVbovFOiC3oAxWPxcdS9bTQaixt7I8uKSvBzj+PBX/3aX9sOOmFFWldnb4yglPx2YHW/4DPCyJykrmVLdjszYGaKbeFJf7QmR1NZrqeXprrbcvxkQw124kTj4C8/xwg7ZHjdArgbkaVmDNDu9+cZTSszlPwvRm/6UTqdZoRnUy2NepuWIb+qWN4yh13WeHeMqZ3mdoeBQIjoXFvKcDyiCiZi1QTGshdKK7/6Kuo6eDSFB0pAueqGV9oXdqDqLQx2NvjG/1/f7ARoVKwnoP0hyhzmWQn/zPzCl5RbKIds31GD/zTCUM3jgibgmvcQwHnlHQzJkjYiYz8zyMqfQRMWMV71wIqJxHRbb2a78j3QCa7uPBAih5WHdBqdxMg9qlAUKeb6HcxgI3T73KM5mkrdZA="
                ));
                break;
            }
            case DyeColor.PINK: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTA0MDk5ODE0MywKICAicHJvZmlsZUlkIiA6ICJjYmNkNDQzZGE1NTI0OGU3ODM3NWNmZjYwMmQzZWI0NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJPX1JlaSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81MWZlNDM4ODM0NDQ3YmFkOTY0M2Y3ZGQyNDBjYzc0Nzg5MzZlZjk5MGVjMGI2ZGVlNGVkM2QwMzEyZGVmOGFmIgogICAgfQogIH0KfQ==",
                        "Z9lD7khC/RrSbw+gMXPyY/uhEWHDERxc1aLavlTTI25LWbG4VFb3DbECCVI+W5lPG81c3BrTkfpWm1O2apjaMoAn0uH80D77rU8kfcyjnuoaHC3uYvdlUqMd+nYyxWRrrJ79tGmPGAF3BL5SGqRgOM6bKgUcf1PAnv8zKVPYXxm/t7k1c+L8B/e6Wi8yHSUCTEH/CYCi5yjtvtMcYv97lgoD0GIAsutSg3BdwREtdgJxxXiH2lzPzpMVaB+mhuCJ/vxlB0g/Pj7nJr3TXtC9nu3BIe/PSdPxUCxrFR9qHpVtMrwE6Z4Y0UShWjyIcHVD8uWjkgS7avyd9hmMSPVMv3M8T7FN2+xQLR//1qeIMs6uL3rD8JOyFItwO0mOj1oj5FdjywPH5FQlsu5E9k52Bmg7T5wG2kJMWdMwXNj6bmPiS2AQ0axNl6JrLlrA5ghUmmNMjgVpUI1ndHT7Puyx0JnKIdX72fAPlOZA0HNhdOkaxtn2cm760Cc3pLJcjhTjwiuEXUxqjRsRAarbL7Rt/o3BNxc+z4uaqhOO2wMg2Sh2+SAENXxwiZZbHoaCOaAC9k9KHQOb2uOV6PDg259XyDHIG+W7umgRcuv2BjHfOyAkHJGgvjAcOqt2sYz4MUcxR7/ZThVUgQSgb1fu2TPJwNIdtCQJIenjE+rsbZLBRQ0="
                ));
                break;
            }
            case DyeColor.RED: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTI4Njg1MTg2OSwKICAicHJvZmlsZUlkIiA6ICIwZWQ2MDFlMDhjZTM0YjRkYWUxZmI4MDljZmEwNTM5NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJOZWVkTW9yZUFjY291bnRzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E1ZGU1ODNmY2VkYjhmMzQ1M2Q5OTMwOTZkMDcwMDVjOWNiMmQxNDQ5M2RjMjEzYjM0NjEwYThjN2FlODVkMTUiCiAgICB9CiAgfQp9",
                        "I0Po9Tzj3F7w1EVAOOVTk+AvSOFix6uJfou2Drj8iuNBD61AiD9FsaDij5VPyqzXVGcXVSFX53qPElsKQzXJBXH7QuOra62HIpeukwmpE1l9u0IyMSEfEpUq5cdCq2ipDbxR49UKK/YcDSB4oyoVnqAmc1UyimEzmS6/VUDglHnN57Tgkg/n5Iss1LA0rvUzcTK0v2moOnYKh7hhNUOsV5/pg+PRC0SQRGzJFUyrR3PWl3uIK2WfPYG9BoOd3x3AbQvyEPRxQUt5sufbQcUZbc9goiJ3WwcYNQc8CaW2kfJuUYjZzOdQhtOYxjcddrWVyD3FxLQJI4u3aJCpMhSigNDcLCdCJvHqguIUzy3Vyvjqy06REl0ACWSNcDXEqXCBOTtye9egRohYwzA2tfjCM+hqIbvwFwao3yIDkUSfP2djbq6sGFk4KYJw/USD3eGj9iza/qq0Kdj20SCzGaBH9LgKVlmtnL4p+ER6xa+OtUYfZaF96NFZBumT+oOiVgbm60npEEyyX5mXFuXjNx9MOevY5I7ZLNn/cNh3V2DevLG4DBj1oMKaVAn4Df1XUydV4q0O07bYVX/93BA4lE2xjQSIgo4pxJXSWiDD8UhkcEQaUkdqOcDJmyijPq2T/l3TKPC7MssnbdblwPZtE/C6ftEFmSTQAe+KmBm+J7gJQoA="
                ));
                break;
            }
            case DyeColor.PURPLE: { // OK
                props.put("textures", new Property("textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTcyOTI4NzAyMzM5MywKICAicHJvZmlsZUlkIiA6ICJiZmQ3MjMxMGNmYWY0Yjc5OTNlYzhiYzU3ODg3YzU5ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbHBoYVNwQW0iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDhlOTlhNGQxZmU1MTdmNjkxYzQ1NmNhOGZiYjg0MGRkMGUxMTc5OTExNmY3N2YyZmY0MWY4MDY2MzY0NWZiIgogICAgfQogIH0KfQ==",
                        "vgClfsZo05vDb/kJ2AEnx7ixO1ya5J8IOkctmTFO6p447+FRZ/RUMlaGWCOGwAmYB8DsV3VDjxeXcqwiQjeM5W2z/2JkeJw2ataxlacn0RlW/qOGG4+8pucvQM/hupPPbx3dan3azYaOtoVlm/kS+1fyPTnJD1IPdgwr3My63kohbDfW9sOLkTpq834eCtK0T+wNHX/c/Hs+j6vsXNNJMXzPREy6WkaZA9TViwCxIu1KbAC8ls/jIWYOiFePlkF9zhcAaY1m95cw7QJLeGanDme8sHTIQdHIeGnvXepy35KyZlrotMqeyeanwAkeVZSTBO3j+i0QTalKNFNtoXf+xZEQTgSeGTI/9XVkaQyT9GziG4F0ag/plBqoJrd2pvlo0yfZDbGDkFFz1bz1appyBiEgK5YM3tUOZiXSHlW1m67beochMEGIACXUCxi3bq+vKlWVErTXdQieT9Zu1N8iqHc6xMF7bm9XW977KJH9qftRgjD4VvAGGposEHWhFKUQxYq9lsugVot0MPkDGFWfNwhPIuW4CQkyV1vYkbll80Kgx6SgPmdBP1PYK3qhSzmaE8Lx5juqGBvw3WzapGKns2rogD0ku5ONdL7+Ree8MUmCNX+TzQW9EiauQRAnLfYdVp95ouDhEN071pPIlvQ4qhOduOx5UHjU0/9OBcupj3Q="
                ));
                break;
            }
        }

        return new GameProfile(gameprofile.id(), gameprofile.name(), new PropertyMap(ImmutableMultimap.copyOf(props.entrySet())));
    }
}
