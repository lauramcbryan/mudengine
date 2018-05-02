backend "consul" {
  address = "jeremias:8500"
}

listener "tcp" {
 address = "jeremias:8200"
 tls_disable = 1
}
listener "tcp" {
 address = "192.168.1.10:8200"
 tls_disable = 1
}

disable_mlock=true